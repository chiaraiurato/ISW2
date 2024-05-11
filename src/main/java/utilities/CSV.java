package utilities;

import model.Release;
import model.Ticket;
import org.uniroma2.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CSV {

    private static final Logger logger = LoggerFactory.getLogger(CSV.class);
    private static FileWriter fileWriter = null;

    public static void createFileCSVForVersion (String projName, List<Release> releaseList) {
        try {
            fileWriter = null;
            String output = projName + "VersionInfo.csv";
            //Name of CSV for output
            fileWriter = new FileWriter(output);
            fileWriter.append("Index,Version ID,Version Name,Date");
            fileWriter.append("\n");
            int i;
            for (i = 0; i < releaseList.size(); i++) {
                int index = i + 1;
                fileWriter.append(Integer.toString(index));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(releaseList.get(i).id()));
                fileWriter.append(",");
                fileWriter.append(releaseList.get(i).getReleaseName());
                fileWriter.append(",");
                fileWriter.append(releaseList.get(i).getReleaseDate().toString());
                fileWriter.append("\n");
            }

        } catch (Exception e) {
            logger.info("Error in csv writer", e);
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                logger.info("Error while flushing/closing fileWriter !!!", e);
            }
        }
    }
    public static void createFileCSVForTicket (String projName, List<Ticket> ticketList) {
        try {
            fileWriter = null;
            String output = projName + "TicketInfo.csv";
            //Name of CSV for output
            fileWriter = new FileWriter(output);
            fileWriter.append("TicketKey,CreationDate,ResolutionDate, OV,FV,AV");
            fileWriter.append("\n");
            int i;
            for (i = 0; i < ticketList.size(); i++) {
                int index = i + 1;
                fileWriter.append(ticketList.get(i).getTicketKey());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(ticketList.get(i).getCreationDate()));
                fileWriter.append(",");
                fileWriter.append(ticketList.get(i).getResolutionDate().toString());
                fileWriter.append(",");
                fileWriter.append(Integer.toString(ticketList.get(i).getOpeningVersion().id()));
                fileWriter.append(",");
                fileWriter.append(Integer.toString(ticketList.get(i).getFixedVersion().id()));
                fileWriter.append(",");
                fileWriter.append(Integer.toString(ticketList.get(i).getAffectedVersions().size()));
                fileWriter.append("\n");
            }

        } catch (Exception e) {
            logger.info("Error in csv writer", e);
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                logger.info("Error while flushing/closing fileWriter !!!", e);
            }
        }
    }
}
