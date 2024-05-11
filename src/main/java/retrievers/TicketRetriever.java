package retrievers;

import model.Release;
import model.Ticket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utilities.JSON;

import java.io.*;
import java.net.URISyntaxException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TicketRetriever {

    private final String projName;
    public List<Release> releaseList;


    /**
     * This is the constructor that you have to use for retrieve tickets without applying cold start.
     *
     * @param projectName The project name from which retrieve tickets.
     * @param releaseList The releases to map into ticket.
     */
    public TicketRetriever(String projectName, List<Release> releaseList) {
        this.projName = projectName;
        this.releaseList = releaseList;
    }


    public List<Ticket> getTickets() throws IOException, URISyntaxException {
        int j;
        int i = 0;
        int total;
        List<Ticket> ticketsList = new ArrayList<>();
        do {
            j = i + 1000;
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + this.projName + "%22AND%22issueType%22=%22Bug%22AND" +
                    "(%22status%22=%22Closed%22OR%22status%22=%22Resolved%22)" +
                    "AND%22resolution%22=%22Fixed%22&fields=key,versions,created,resolutiondate&startAt="
                    + i + "&maxResults=" + j;
            JSONObject json = JSON.readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("issues");
            total = json.getInt("total");
            //Iterate through each bug
            for (; i < total && i < j; i++) {
                // First we get key
                String key = issues.getJSONObject(i % 1000).get("key").toString();
                // Next, we get the creation and resolution date
                JSONObject fields = issues.getJSONObject(i%1000).getJSONObject("fields");
                String creationDateString = fields.get("created").toString();
                String resolutionDateString = fields.get("resolutiondate").toString();
                LocalDate creationDate = LocalDate.parse(creationDateString.substring(0,10));
                LocalDate resolutionDate = LocalDate.parse(resolutionDateString.substring(0,10));

                // Get the versions affected by the issue
                JSONArray affectedVersionsArray = fields.getJSONArray("versions");

                // Get the OV
                Release openingVersion = Release.getReleaseAfterOrEqualDate(creationDate, this.releaseList);

                // Get the FV
                Release fixedVersion = Release.getReleaseAfterOrEqualDate(resolutionDate, this.releaseList);

                List<Release> affectedVersionsList = Release.getValidAffectedVersions(affectedVersionsArray, this.releaseList);
                if(!affectedVersionsList.isEmpty()
                        && openingVersion!=null
                        && fixedVersion!=null
                        //Check that OV<=AV0 where AV0 is the first affected version
                        && (!affectedVersionsList.get(0).getReleaseDate().isBefore(openingVersion.getReleaseDate())
                        // Check that FV > OV
                        || openingVersion.getReleaseDate().isAfter(fixedVersion.getReleaseDate()))){
                    continue;
                }
                if(openingVersion != null && fixedVersion != null && openingVersion.id()!=releaseList.get(0).id()){
                    //At this point should be true that OV < FV
                    ticketsList.add(new Ticket(key, creationDate, resolutionDate, openingVersion, fixedVersion, affectedVersionsList));
                }
            }
        } while (i < total);

        //return fixTicketList(ticketsList, this.releaseList);
        return ticketsList;

    }

//    private List<Ticket> fixTicketList(List<Ticket> ticketsList, List<Release> releasesList) throws URISyntaxException {
//        List<Ticket> ticketsForProportionList = new ArrayList<>();
//        List<Ticket> finalTicketsList = new ArrayList<>();
//
//        for (Ticket ticket : ticketsList) {
//            if (!ticket.getAffectedVersions().isEmpty()) {
//                float proportion = ComputeProportion.computeProportion(ticketsForProportionList, this.projName, ticket, true);
//                fixTicketWithProportion(ticket, releasesList, proportion);
//                completeAffectedVersionsList(ticket, releasesList);
//            } else {
//                ComputeProportion.computeProportion(ticketsForProportionList, this.projName, ticket, false);
//                completeAffectedVersionsList(ticket, releasesList);
//            }
//            ticketsForProportionList.add(ticket);
//            finalTicketsList.add(ticket);
//        }
//
//        finalTicketsList.sort(Comparator.comparing(Ticket::getResolutionDate));
//        return finalTicketsList;
//    }




}
