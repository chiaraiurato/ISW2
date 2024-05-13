package utilities;

import model.ClassProject;
import model.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ARFF {
    /**
     * Utility class to generate ARFF Files
     */

    private static File file;

    public static void createFileArff(String projName, List<Release> releaseList, List<ClassProject> classProjectList, String type, int i) {
        String path ="output/arff/"+ projName+ "/"+type;
        String filename = "/"+projName+"_"+type+i+".arff";
        file = new File(path);
        try{
            if (!file.exists()) {
                boolean created = file.mkdirs();
                if (!created) {
                    throw new IOException();
                }
            }
            file = new File(path+filename);
            try(FileWriter writer = new FileWriter(file)) {
                writer.append("@relation ").append(filename).append("\n\n")
                        .append("""
                        @attribute SIZE numeric
                        @attribute LOC_ADDED numeric
                        @attribute LOC_ADDED_AVG numeric
                        @attribute LOC_ADDED_MAX numeric
                        @attribute LOC_REMOVED numeric
                        @attribute LOC_REMOVED_AVG numeric
                        @attribute LOC_REMOVED_MAX numeric
                        @attribute CHURN numeric
                        @attribute CHURN_AVG numeric
                        @attribute CHURN_MAX numeric
                        @attribute LOC_TOUCHED numeric
                        @attribute LOC_TOUCHED_AVG numeric
                        @attribute LOC_TOUCHED_MAX numeric
                        @attribute NUMBER_OF_REVISIONS numeric
                        @attribute NUMBER_OF_DEFECT_FIXES numeric
                        @attribute NUMBER_OF_AUTHORS numeric
                        @attribute IS_BUGGY {'YES', 'NO'}
                        
                        @data
                        """);
                appendData(releaseList, classProjectList, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void appendData(List<Release> releaseList, List<ClassProject> classProjectList, FileWriter fileWriter) throws IOException {
        for (Release release : releaseList) {
            for (ClassProject classProject : classProjectList) {
                if (classProject.getRelease().id() == release.id()) {
                    appendEntries(fileWriter,classProject);
                }
            }
        }
    }

    private static void appendEntries(FileWriter fileWriter,ClassProject classProject) throws IOException {
        String isClassBugged = classProject.getMetric().getBuggyness() ? "YES" : "NO" ;

        fileWriter.append(classProject.getMetric().getSize()).append(",")
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

}
