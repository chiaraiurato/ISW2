package retrievers;

import model.Commit;
import model.Ticket;
import model.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CommitRetriever {
    private List<Ticket> ticketList;

    private final List<Version> releaseList;
    protected final Git git;
    private final Repository repository;
    public CommitRetriever(String projName, String repoURL, List<Version> releaseList) throws IOException, GitAPIException {
        String filename = projName.toLowerCase() + "Temp";
        File directory = new File(filename);
        if(directory.exists()){
            repository = new FileRepository(filename + "\\.git");
            git = new Git(repository);
        }else{
            git = Git.cloneRepository().setURI(repoURL).setDirectory(directory).call();
            repository = git.getRepository();
        }
        this.releaseList = releaseList;
        this.ticketList = null;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }
    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    public List<Version> getReleaseList() {
        return releaseList;
    }

    public List<Commit> extractAllCommits() throws IOException, GitAPIException, ParseException {
        List<RevCommit> revCommitList = new ArrayList<>();
        //List<Ref> branchList = this.git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        revCommitList.sort(Comparator.comparing(o -> o.getCommitterIdent().getWhen()));
        List<Commit> commitList = new ArrayList<>();
        for (RevCommit revCommit : revCommitList) {
            Date commitDate = revCommit.getCommitterIdent().getWhen();
            Date lowerBoundDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
            for(Version version: this.releaseList){
                //if lowerBoundDate < commitDate <= releaseDate then the revCommit has been done in that release
                if (commitDate.after(lowerBoundDate) && !commitDate.after(Date.from(version.releaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    Commit newCommit = new Commit(revCommit, version);
                    commitList.add(newCommit);
                    version.addCommit(newCommit);
                }
                lowerBoundDate = Date.from(version.releaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

        }
        commitList.sort(Comparator.comparing(o -> o.getRevCommit().getCommitterIdent().getWhen()));
        return commitList;
    }
}
