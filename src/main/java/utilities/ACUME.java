package utilities;

import exception.AcumeFileException;
import model.AcumeFile;
import model.WekaClassifier;

import java.io.*;
import java.util.List;
import java.util.Objects;


public class ACUME {

    private ACUME() {
    }

    public static void createFileForAcume(String projName, WekaClassifier wekaClassifier, Integer index, List<AcumeFile> acumeFiles) throws AcumeFileException {

        try {
            File file = new File("output/acume/" + projName);
            if (!file.exists()) {
                boolean success = file.mkdirs();
                if (!success) {
                    throw new IOException();
                }
            }
            StringBuilder fileName = new StringBuilder();
            String filter;
            String featureSelectionFilterName = wekaClassifier.getFeatureSelectionFilterName();
            if(Objects.equals(featureSelectionFilterName, "BestFirst(Bi-directional)"))
                filter = "true";
            else
                filter = "false";
            String sampling;
            if(Objects.equals(wekaClassifier.getSamplingFilterName(), "NoSampling"))
                sampling = "NotSet";
            else
                sampling = wekaClassifier.getSamplingFilterName();
            fileName.append("/").append(projName).append("_").append(wekaClassifier.getClassifierName()).append(filter).append(sampling).append(wekaClassifier.isCostSensitive()).append(index).append(".csv");
            file = new File("output/acume/" + projName + fileName);
          writeToCsv(file, acumeFiles);

        } catch (IOException | RuntimeException e) {
            throw new AcumeFileException("Error while creating csv file");
        }
    }
    public static void writeToCsv(File file, List<AcumeFile> acumeFiles) throws AcumeFileException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.append("ID," +
                    "SIZE," +
                    "PREDICTED," +
                    "ACTUAL\n");
            for (AcumeFile acumeFile : acumeFiles) {

                fileWriter.write(acumeFile.getIndex() + ",");                          //INDEX OF CLASS
                fileWriter.write(acumeFile.getSize() + ",");                           //SIZE OF CLASS
                fileWriter.write(acumeFile.getProbabilityOfBuggyness() + ",");         //PROBABILITY OF BUGGY
                fileWriter.write(acumeFile.isBuggy() + "\n");

            }
        } catch (IOException e) {
            throw new AcumeFileException("Error while create cvs for acume");
        }
    }
}