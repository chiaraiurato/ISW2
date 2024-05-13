package utilities;

import exception.TxtFileException;
import model.Commit;
import model.Release;
import model.Ticket;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TXT {

    private final String projName;
    private File file;

    public TXT(String projName) {
    this.projName=projName;
    this.file = new File("output/list/" + projName);
    }

    private  <T> void printListToFile(List<T> list, String filename) throws TxtFileException {
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
            throw new TxtFileException("Txt file creation error:"+e);
        }
    }

    public void begin(List<Release> releaseList, List<Commit> commitList, List<Ticket> ticketList, List<Commit> filteredCommits) throws IOException, TxtFileException {
        printListToFile(releaseList, "ReleaseList.txt");
        printListToFile(commitList, "CommitList.txt");
        printListToFile(ticketList, "TicketList.txt");
        printListToFile(filteredCommits, "CommitBuggyList.txt");
    }
}
