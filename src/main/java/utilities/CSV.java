package utilities;

import model.Release;
import org.example.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSV {

    private static final Logger logger = LoggerFactory.getLogger(Execute.class);
    private static FileWriter fileWriter = null;

    public static void createFileCSV(String projName, List<Release> releaseList) {
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
                fileWriter.append(releaseList.get(i).releaseName());
                fileWriter.append(",");
                fileWriter.append(releaseList.get(i).releaseDate().toString());
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
