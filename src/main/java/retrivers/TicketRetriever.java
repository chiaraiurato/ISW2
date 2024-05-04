package retrivers;

import model.SimpleTicket;
import model.Ticket;
import model.Version;
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

    //  List<Ticket> tickets;


    /**
     * This is the constructor that you have to use for retrieve tickets without applying cold start.
     *
     * @param projectName The project name from which retrieve tickets.
     */
    public TicketRetriever(String projectName) throws IOException, URISyntaxException {
        this.projName = projectName;
        init(projectName);

    }


    private void init(String projName) throws IOException, URISyntaxException {
        getBugTickets();

    }

    public List<Version> extractAllReleases() throws IOException, JSONException, URISyntaxException {
        List<Version> releaseList = new ArrayList<>();
        int i = 0;
        String url = "https://issues.apache.org/jira/rest/api/latest/project/" + this.projName;
        JSONObject json = JSON.readJsonFromUrl(url);
        JSONArray versions = json.getJSONArray("versions");
        for (; i < versions.length(); i++) {
            String releaseName;
            String releaseDate;
            JSONObject releaseJsonObject = versions.getJSONObject(i);
            if (releaseJsonObject.has("releaseDate") && releaseJsonObject.has("name")) {
                releaseDate = releaseJsonObject.get("releaseDate").toString();
                releaseName = releaseJsonObject.get("name").toString();
                releaseList.add(new Version(releaseName, LocalDate.parse(releaseDate)));
            }
        }
        releaseList.sort(Comparator.comparing(Version::releaseDate));
        i = 0;
        for (Version version : releaseList) {
            version.setId(++i);
        }
        return releaseList;
    }

    public List<SimpleTicket> extractAll(List<Version> releasesList) throws IOException, JSONException, URISyntaxException {

        // !TODO = Adesso stai prendendo tutti i ticket, ma ne devi fare proportion dopo

        return getBugTickets();
    }


        public List<SimpleTicket> getBugTickets() throws IOException, URISyntaxException {
            int j;
            int i = 0;
            int total;
            List<SimpleTicket> ticketsList = new ArrayList<>();
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
                for (; i < total && i < j; i++) {
                    //Iterate through each bug
                    String key = issues.getJSONObject(i % 1000).get("key").toString();
                    ticketsList.add(new SimpleTicket(key));
                    //JSONObject fields = issues.getJSONObject(i%1000).getJSONObject("fields");
//                String creationDateString = fields.get("created").toString();
//                String resolutionDateString = fields.get("resolutiondate").toString();
//                LocalDate creationDate = LocalDate.parse(creationDateString.substring(0,10));
//                LocalDate resolutionDate = LocalDate.parse(resolutionDateString.substring(0,10));
//                JSONArray affectedVersionsArray = fields.getJSONArray("versions");
//                Version openingVersion = ReleaseUtilities.getReleaseAfterOrEqualDate(creationDate, releasesList);
//                Version fixedVersion =  ReleaseUtilities.getReleaseAfterOrEqualDate(resolutionDate, releasesList);
//                List<Version> affectedVersionsList = ReleaseUtilities.returnValidAffectedVersions(affectedVersionsArray, releasesList);
//                if(!affectedVersionsList.isEmpty()
//                        && openingVersion!=null
//                        && fixedVersion!=null
//                        && (!affectedVersionsList.get(0).releaseDate().isBefore(openingVersion.releaseDate())
//                        || openingVersion.releaseDate().isAfter(fixedVersion.releaseDate()))){
//                    continue;
//                }
//                if(openingVersion != null && fixedVersion != null && openingVersion.id()!=releasesList.get(0).id()){
//                    ticketsList.add(new Ticket(key, creationDate, resolutionDate, openingVersion, fixedVersion, affectedVersionsList));
//                }
                }
            } while (i < total);

            return ticketsList;

        }

}
