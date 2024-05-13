package utilities;

import exception.CsvFileException;
import model.ClassProject;
import model.Release;
import model.Ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSV {
    /**
     * Utility class to generate CSV files
     */
    private CSV(){

    }
    private static final Logger logger = LoggerFactory.getLogger(CSV.class);

    public static void createFileCsv(String projName, List<Release> releaseList, List<ClassProject> classProjectList, String type, int i) throws CsvFileException {
        //Build path
        String[] pathComponents = {"output", "csv", projName, type};
        String path = String.join(File.separator, pathComponents);

        // Build filename
        String filename = File.separator + projName + "_" + type + i + ".csv";
        File file = new File(path);
        try{
            if (!file.exists()) {
                boolean created = file.mkdirs();
                if (!created) {
                    throw new IOException();
                }
            }
            file = new File(path+filename);
            try(FileWriter writer = new FileWriter(file)) {
                writer.append("RELEASE_ID," +
                        "FILE_NAME," +
                        "SIZE," +
                        "LOC_ADDED,LOC_ADDED_AVG,LOC_ADDED_MAX," +
                        "LOC_REMOVED,LOC_REMOVED_AVG,LOC_REMOVED_MAX," +
                        "LOC_TOUCHED,LOC_TOUCHED_AVG,LOC_TOUCHED_MAX," +
                        "CHURN,CHURN_AVG,CHURN_MAX," +
                        "NUMBER_OF_REVISIONS," +
                        "NUMBER_OF_DEFECT_FIXES," +
                        "NUMBER_OF_AUTHORS," +
                        "IS_BUGGY").append("\n");
                appendData(releaseList, classProjectList, writer);
            }
        } catch (IOException e) {
            throw new CsvFileException("Csv file creation error" + e);
        }
    }
    private static void appendData(List<Release> releaseList, List<ClassProject> classProjectList, FileWriter fileWriter) throws IOException {
        for (Release release : releaseList) {
            for (ClassProject classProject : classProjectList) {
                if (classProject.getRelease().id() == release.id()) {
                    appendEntries(fileWriter, release, classProject);
                }
            }
        }
    }

    private static void appendEntries(FileWriter fileWriter,Release release,ClassProject classProject) throws IOException {
        String isClassBugged = classProject.getMetric().getBuggyness() ? "YES" : "NO" ;
        fileWriter.append(String.valueOf(release.id())).append(",")
                .append(classProject.getKey()).append(",")
                .append(classProject.getMetric().getSize()).append(",")
                .append(classProject.getMetric().getAddedLOCMetrics()).append(",")
                .append(classProject.getMetric().getAvgAddedLOCMetrics()).append(",")
                .append(classProject.getMetric().getMaxAddedLOCMetrics()).append(",")
                .append(classProject.getMetric().getRemovedLOCMetrics()).append(",")
                .append(classProject.getMetric().getAvgRemovedLOCMetrics()).append(",")
                .append(classProject.getMetric().getMaxRemovedLOCMetrics()).append(",")
                .append(classProject.getMetric().getTouchedLOCMetrics()).append(",")
                .append(classProject.getMetric().getAvgTouchedLOCMetrics()).append(",")
                .append(classProject.getMetric().getMaxTouchedLOCMetrics()).append(",")
                .append(classProject.getMetric().getChurnMetrics()).append(",")
                .append(classProject.getMetric().getAvgChurnMetrics()).append(",")
                .append(classProject.getMetric().getMaxChurnMetrics()).append(",")
                .append(classProject.getMetric().getStringNumberOfRevisions()).append(",")
                .append(classProject.getMetric().getStringNumberOfDefectFixes()).append(",")
                .append(classProject.getMetric().getStringNumberOfAuthors()).append(",")
                .append(isClassBugged).append("\n");
    }

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

    private static void closeFileWriter(FileWriter fileWriter) {
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

