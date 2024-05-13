package utilities;

import model.Commit;
import model.Release;
import model.Ticket;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CreateReportFile {

    private final String projName;
    private File file;

    public CreateReportFile(String projName) {
    this.projName=projName;
    this.file = new File("output/list/" + projName);
    }

    private  <T> void printListToFile(List<T> list, String filename){
        try{
            if (!file.exists()) {
                boolean created = file.mkdirs();
                if (!created) {
                    throw new IOException();
                }
            }
            file = new File("output/list/" + projName + "/"+ filename);
            try(FileWriter writer = new FileWriter(file)) {
                writer.append("******** ").append(this.projName).append("/").append(filename).append(" ******");
                writer.append("\nTOTAL NUMBER:").append(String.valueOf(list.size()));
                writer.append("\n\n");
                for (T item : list) {
                    writer.write(item.toString());
                    writer.append("\n\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void begin(List<Release> releaseList, List<Commit> commitList, List<Ticket> ticketList, List<Commit> filteredCommits) throws IOException {
        printListToFile(releaseList, "ReleaseList.txt");
        printListToFile(commitList, "CommitList.txt");
        printListToFile(ticketList, "TicketList.txt");
        printListToFile(filteredCommits, "CommitBuggyList.txt");
    }
}
