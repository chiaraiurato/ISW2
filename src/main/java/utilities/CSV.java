package utilities;

import model.Version;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSV {

    private static FileWriter fileWriter = null;

    public static void createFileCSV(String projName, List<Version> versionList) {
        try {
            fileWriter = null;
            String outname = projName + "VersionInfo.csv";
            //Name of CSV for output
            fileWriter = new FileWriter(outname);
            fileWriter.append("Index,Version ID,Version Name,Date");
            fileWriter.append("\n");
            int i;
            for (i = 0; i < versionList.size(); i++) {
                Integer index = i + 1;
                fileWriter.append(index.toString());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(versionList.get(i).id()));
                fileWriter.append(",");
                fileWriter.append(versionList.get(i).releaseName());
                fileWriter.append(",");
                fileWriter.append(versionList.get(i).releaseDate().toString());
                fileWriter.append("\n");
            }

        } catch (Exception e) {
            System.out.println("Error in csv writer");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }
}
