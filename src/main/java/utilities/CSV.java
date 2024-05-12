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
        createCSVFile(projName + "VersionInfo.csv", "Index,Version ID,Version Name,Date",
                releaseList, (release, i) -> {
                    try {
                        fileWriter.append(Integer.toString(i + 1));
                        fileWriter.append(",");
                        fileWriter.append(String.valueOf(release.id()));
                        fileWriter.append(",");
                        fileWriter.append(release.getReleaseName());
                        fileWriter.append(",");
                        fileWriter.append(release.getReleaseDate().toString());
                        fileWriter.append("\n");
                    } catch (IOException e) {
                        logger.error("Error writing CSV file", e);
                    }
                });
    }

    public static void createFileCSVForTicket(String projName, List<Ticket> ticketList) {
        createCSVFile(projName + "TicketInfo.csv", "TicketKey,CreationDate,ResolutionDate, OV,FV,AV",
                ticketList, (ticket, i) -> {
                    try {
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
                    } catch (IOException e) {
                        logger.error("Error writing CSV file", e);
                    }
                });
    }

    private static <T> void createCSVFile(String filename, String header, List<T> dataList, CSVLineWriter<T> lineWriter) {
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.append(header);
            fileWriter.append("\n");
            for (int i = 0; i < dataList.size(); i++) {
                lineWriter.write(dataList.get(i), i);
            }
        } catch (IOException e) {
            logger.error("Error creating CSV file", e);
        } finally {
            closeFileWriter();
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

    private interface CSVLineWriter<T> {
        void write(T item, int index) throws IOException;
    }
}

