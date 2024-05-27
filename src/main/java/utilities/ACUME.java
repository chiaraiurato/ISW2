package utilities;

import model.AcumeFile;
import model.WekaClassifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ACUME {

    private ACUME() {
    }

    public static void createFileForAcume(String projName, WekaClassifier wekaClassifier, Integer index, List<AcumeFile> acumeFiles) throws IOException {

        //String fileName = projName.toUpperCase() + "_" + classifierEnum.toString().toUpperCase() + "_" + featureSelectionEnum.toString().toUpperCase() + "_" + samplingEnum.toString().toUpperCase() + "_" + costSensitiveEnum.toString().toUpperCase() + "_" + index.toString();
        try {
            File file = new File("output/acume/" + projName);
            if (!file.exists()) {
                boolean success = file.mkdirs();
                if (!success) {
                    throw new IOException();
                }
            }
            StringBuilder fileName = new StringBuilder();
            fileName.append("/").append(projName).append("_").append(wekaClassifier.getClassifierName()).append(wekaClassifier.getFeatureSelectionFilterName()).append(wekaClassifier.getSamplingFilterName()).append(wekaClassifier.isCostSensitive()).append(index).append(".csv");
            file = new File("output/acume/" + projName + fileName);
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
                throw new RuntimeException(e);
            }


        } catch (IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}