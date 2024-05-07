package retrievers;

import model.Commit;
import model.Release;
import model.Ticket;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CommitRetriever {
    private final Repository repo;
    private final Git git;
    private List<Ticket> ticketList;

    private final List<Release> releaseList;
    public CommitRetriever(String projName,String projURL, List<Release> releaseList) throws IOException, GitAPIException {
        File directory = new File("temp/" + projName); //Create the directory where to clone repo

        if (directory.exists()) {
            this.repo = new FileRepository("temp/" + projName + "/.git");
            this.git = new Git(this.repo);
        } else {

            this.git = Git.cloneRepository()
                    .setURI(projURL)
                    .setDirectory(
                            directory)
                    .call();

            this.repo = git.getRepository();
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

    public List<Release> getReleaseList() {
        return releaseList;
    }

    public List<Commit> extractAllCommits() throws IOException, GitAPIException, ParseException {
        List<RevCommit> revCommitList = new ArrayList<>();
        List<Ref> branchesList = this.git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref branch : branchesList) {
            Iterable<RevCommit> commitsBranchesList = this.git.log().add(this.repo.resolve(branch.getName())).call();

            for (RevCommit commit : commitsBranchesList) {
                if (!revCommitList.contains(commit)) {
                    revCommitList.add(commit);
                }
            }
        }
        revCommitList.sort(Comparator.comparing(o -> o.getCommitterIdent().getWhen()));
        List<Commit> commitList = new ArrayList<>();
        for (RevCommit revCommit : revCommitList) {
            Date commitDate = revCommit.getCommitterIdent().getWhen();
            Date lowerBoundDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
            for(Release release : this.releaseList){
                //if lowerBoundDate < commitDate <= releaseDate then the revCommit has been done in that release
                if (commitDate.after(lowerBoundDate) && !commitDate.after(Date.from(release.releaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    Commit newCommit = new Commit(revCommit, release);
                    commitList.add(newCommit);
                    release.addCommit(newCommit);
                }
                lowerBoundDate = Date.from(release.releaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

        }
        commitList.sort(Comparator.comparing(o -> o.getRevCommit().getCommitterIdent().getWhen()));
        releaseList.removeIf(release -> release.getCommitList().isEmpty());
        int i = 0;
        for (Release release : releaseList) {
            release.setId(++i);
        }
        commitList.sort(Comparator.comparing(o -> o.getRevCommit().getCommitterIdent().getWhen()));
        return commitList;
    }
}
