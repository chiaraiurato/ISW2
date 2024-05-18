package utilities;

import exception.CsvFileException;
import model.ClassProject;
import model.Release;
import model.ResultOfClassifier;
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

    public static void createFinalCsv(String projName, List<ResultOfClassifier> finalResultsList){
        try {
            File file = new File("finalResults/" + projName );
            if (!file.exists()) {
                boolean success = file.mkdirs();
                if (!success) {
                    throw new IOException();
                }
            }
            StringBuilder fileName = new StringBuilder();
            fileName.append("/").append(projName).append("_finalReport").append(".csv");
            file = new File("finalResults/" + projName + fileName);
            try(FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.append("DATASET," +
                        "#TRAINING_RELEASES," +
                        "%TRAINING_INSTANCES," +
                        "CLASSIFIER," +
                        "FEATURE_SELECTION," +
                        "BALANCING," +
                        "COST_SENSITIVE," +
                        "PRECISION," +
                        "RECALL," +
                        "AREA_UNDER_ROC," +
                        "KAPPA," +
                        "TRUE_POSITIVES," +
                        "FALSE_POSITIVES," +
                        "TRUE_NEGATIVES," +
                        "FALSE_NEGATIVES").append("\n");
                for(ResultOfClassifier resultOfClassifier: finalResultsList){
                    fileWriter.append(projName).append(",")
                            .append(String.valueOf(resultOfClassifier.getWalkForwardIteration())).append(",")
                            .append(String.valueOf(resultOfClassifier.getTrainingPercent())).append(",")
                            .append(resultOfClassifier.getClassifierName()).append(",");
                    if(resultOfClassifier.hasFeatureSelection()){
                        fileWriter.append(resultOfClassifier.getCustomClassifier().getFeatureSelectionFilterName()).append(",");
                    }else {
                        fileWriter.append("None").append(",");
                    }
                    if(resultOfClassifier.hasSampling()){
                        fileWriter.append(resultOfClassifier.getCustomClassifier().getSamplingFilterName()).append(",");
                    }else {
                        fileWriter.append("None").append(",");
                    }
                    if (resultOfClassifier.hasCostSensitive()){
                        fileWriter.append("SensitiveLearning").append(",");
                    }else {
                        fileWriter.append("None").append(",");
                    }
                    fileWriter.append(String.valueOf(resultOfClassifier.getPrecision())).append(",")
                            .append(String.valueOf(resultOfClassifier.getRecall())).append(",")
                            .append(String.valueOf(resultOfClassifier.getAreaUnderROC())).append(",")
                            .append(String.valueOf(resultOfClassifier.getKappa())).append(",")
                            .append(String.valueOf(resultOfClassifier.getTruePositives())).append(",")
                            .append(String.valueOf(resultOfClassifier.getFalsePositives())).append(",")
                            .append(String.valueOf(resultOfClassifier.getTrueNegatives())).append(",")
                            .append(String.valueOf(resultOfClassifier.getFalseNegatives())).append("\n");
                }
                closeFileWriter(fileWriter);
            }
        } catch (IOException e) {
            logger.info("Error in .csv creation when trying to create directory");
        }
    }
    public static void createFileCsv(String projName, List<Release> releaseList, List<ClassProject> classProjectList, String type, int i) throws CsvFileException {
        //Build path
        String[] pathComponents = {"output", "csv", projName, type};
        String path = String.join(File.separator, pathComponents);

        // Build filename
        String filename = File.separator + type + i + ".csv";
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

