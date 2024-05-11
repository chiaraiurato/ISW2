package controllers;

import model.Release;
import model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrievers.ReleaseRetriever;
import retrievers.TicketRetriever;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SetProportion {

    public static final int THRESHOLD_FOR_COLD_START = 5;
    public static final String START_SEPARATOR = "\n{";
    private static final String END_SEPARATOR = "}\n";
    private static final String NEW_LINE = "\n";
    private static final StringBuilder writeToFile = new StringBuilder();
    private static final Logger logger = LoggerFactory.getLogger(SetProportion.class);
    private static Float coldStartComputedProportion = null;
    private enum OtherProjects {
        ZOOKEEPER
    }

    private SetProportion() {}

    private static boolean writeUsedOrNot(Ticket ticket, boolean calculate) {
        //Writes information about whether a ticket is used or not to a file.
        if(!calculate){
            if (ticket.getFixedVersion().id() != ticket.getOpeningVersion().id()) {
                writeToFile.append(START_SEPARATOR).append(ticket.getTicketKey()).append(END_SEPARATOR).append("PROPORTION: FX != OV ").append(NEW_LINE);
            }else{
                writeToFile.append(START_SEPARATOR).append(ticket.getTicketKey()).append(END_SEPARATOR).append("PROPORTION: Denominator = 1 ").append(NEW_LINE);
            }
            return true;
        }
        return false;
    }

    private static float incrementalProportion(List<Ticket> filteredTicketsList, Ticket ticket, boolean writeInfo, boolean doActualComputation) {
        if (writeUsedOrNot(ticket, doActualComputation)) return 0;
        filteredTicketsList.sort(Comparator.comparing(Ticket::getResolutionDate));
        writeToFile.append("\n*** PROPORTION ***\n");
        if (writeInfo) {
            writeToFile.append(START_SEPARATOR).append(ticket.getTicketKey()).append(END_SEPARATOR);
        }
        float totalProportion = 0.0F;
        float denominator;
        for (Ticket correctTicket : filteredTicketsList) {
            if (correctTicket.getFixedVersion().id() != correctTicket.getOpeningVersion().id()) {
                denominator = ((float) correctTicket.getFixedVersion().id() - (float) correctTicket.getOpeningVersion().id());
            }else{
                denominator = 1;
            }
            if(correctTicket.getInjectedVersion()!=null) {
                float propForTicket = ((float) correctTicket.getFixedVersion().id() - (float) correctTicket.getInjectedVersion().id())
                        / denominator;
                totalProportion += propForTicket;
            }
        }
        writeToFile.append("Size of filtered ticket: ").append(filteredTicketsList.size()).append(NEW_LINE);
        float average = totalProportion / filteredTicketsList.size();
        writeToFile.append("PROPORTION AVERAGE: ").append(average).append(NEW_LINE)
                .append("----------------------------------------------------------"+ NEW_LINE);
        return average;
    }

    private static float coldStartProportion(Ticket ticket, boolean doActualComputation) throws IOException, URISyntaxException {
        if (writeUsedOrNot(ticket, doActualComputation)) return 0;
        if(coldStartComputedProportion != null){
            writeToFile.append("\n*** COLD START ***\n");
            writeToFile.append(START_SEPARATOR).append(ticket.getTicketKey()).append(END_SEPARATOR).append("PROPORTION: ").append(coldStartComputedProportion).append(NEW_LINE);
            return coldStartComputedProportion;
        }
        writeToFile.append("\n\nCOLD-START PROPORTION COMPUTATION STARTED -----------------  >\n");
        writeToFile.append(START_SEPARATOR).append(ticket.getTicketKey()).append(END_SEPARATOR);
        List<Float> proportionList = new ArrayList<>();
        for(OtherProjects projName: OtherProjects.values()){
            ReleaseRetriever releaseRetriever = new ReleaseRetriever(projName.toString());
            List<Release> releaseList = releaseRetriever.getVersions();
            TicketRetriever ticketRetriever = new TicketRetriever(projName.toString(), releaseList);
            List<Ticket> ticketCompleteList = ticketRetriever.getTickets();
            List<Ticket> ticketCorrectList = Ticket.getCorrectTickets(ticketCompleteList);
            if(ticketCorrectList.size() >= THRESHOLD_FOR_COLD_START){
                proportionList.add(incrementalProportion(ticketCorrectList, ticket, false, doActualComputation));
            }
        }
        Collections.sort(proportionList);
        writeToFile.append("\nPROPORTION LIST: ").append(" -----------------------------------------------\n")
                .append(proportionList).append("\n");
        float median;
        int size = proportionList.size();
        if (size % 2 == 0) {
            median = (proportionList.get((size / 2) - 1) + proportionList.get(size / 2)) / 2;
        } else {
            median = proportionList.get(size / 2);
        }
        writeToFile.append("MEDIAN PROPORTION OUT OF ALL PROJECTS FOR COLD START: ").append(median).append("\n")
                .append("-----------------------------------------------------------------\n\n\n")
                .append("COLD-START PROPORTION COMPUTATION END <------------------\n\n");
        coldStartComputedProportion = median;
        return median;
    }
    public static float apply(List<Ticket> ticketsList, String projName, Ticket ticket, boolean calculate) throws URISyntaxException {
        float proportion = 0;
        try {
            File file = new File("output/report/" + projName);
            if (!file.exists()) {
                boolean created = file.mkdirs();
                if (!created) {
                    throw new IOException();
                }
            }
            file = new File("output/report/" + projName + "/Proportion.txt");
            try(FileWriter fileWriter = new FileWriter(file)) {
                if (ticketsList.size() >= THRESHOLD_FOR_COLD_START) {
                    proportion = incrementalProportion(ticketsList, ticket, true, calculate);
                } else {
                    proportion = coldStartProportion(ticket, calculate);
                }
                fileWriter.append(writeToFile.toString());
                    fileWriter.flush();
            }
        } catch(IOException e){
            logger.info("Error in ComputeProportion when trying to create directory");
        }
        return proportion;
    }

}
