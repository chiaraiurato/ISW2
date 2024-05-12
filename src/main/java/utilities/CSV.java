package utilities;

import model.Release;
import model.Ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSV {

    private static final Logger logger = LoggerFactory.getLogger(CSV.class);

    private static FileWriter fileWriter = null;

    public static void createFileCSVForVersion(String projName, List<Release> releaseList) {
        String output = projName + "VersionInfo.csv";

        try (FileWriter fileWriter = new FileWriter(output)) {
            fileWriter.append("Index,Version ID,Version Name,Date");
            fileWriter.append("\n");

            for (int i = 0; i < releaseList.size(); i++) {
                int index = i + 1;
                Release release = releaseList.get(i);

                fileWriter.append(Integer.toString(index));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(release.id()));
                fileWriter.append(",");
                fileWriter.append(release.getReleaseName());
                fileWriter.append(",");
                fileWriter.append(release.getReleaseDate().toString());
                fileWriter.append("\n");
            }
        } catch (IOException e) {
            logger.error("Error writing CSV file", e);
        }
    }


    public static void createFileCSVForTicket(String projName, List<Ticket> ticketList) {
        String output = projName + "TicketInfo.csv";

        try (FileWriter fileWriter = new FileWriter(output)) {
            fileWriter.append("TicketKey,CreationDate,ResolutionDate, OV,FV,AV");
            fileWriter.append("\n");

            for (int i = 0; i < ticketList.size(); i++) {
                Ticket ticket = ticketList.get(i);

                fileWriter.append(ticket.getTicketKey());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(ticket.getCreationDate()));
                fileWriter.append(",");
                fileWriter.append(ticket.getResolutionDate().toString());
                fileWriter.append(",");
                fileWriter.append(Integer.toString(ticket.getOpeningVersion().id()));
                fileWriter.append(",");
                fileWriter.append(Integer.toString(ticket.getFixedVersion().id()));
                fileWriter.append(",");
                fileWriter.append(Integer.toString(ticket.getAffectedVersions().size()));
                fileWriter.append("\n");
            }
        } catch (IOException e) {
            logger.error("Error writing CSV file", e);
        }
    }



    private static void closeFileWriter() {
        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            logger.error("Error while flushing/closing fileWriter", e);
        }
    }
}

