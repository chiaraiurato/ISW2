package retrievers;

import model.Commit;
import model.Release;
import model.Ticket;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class CommitRetriever {
    private final Repository repo;
    private final Git git;
    private List<Ticket> ticketList;

    private final List<Release> releaseList;
    /**
     * This is the constructor that you have to use for retrieve commits.
     *
     @param git The Git object representing the project repository URL to clone.
     @param repository The Repository object representing the project repository URL.
     @param releaseList The list of releases associated with the project.
     */
    public CommitRetriever(Git git, Repository repository, List<Release> releaseList){
        this.repo = repository;
        this.git = git;
        this.releaseList = releaseList;
        this.ticketList = null;
    }

    public List<Commit> extractAllCommits() throws IOException, GitAPIException, ParseException {
        List<RevCommit> revCommitList = new ArrayList<>();
        List<Commit> commitList = new ArrayList<>();
        //Iterate through each branch and get all commits
        List<Ref> branchesList = this.git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref branch : branchesList) {
            Iterable<RevCommit> commitsBranchesList = this.git.log().add(this.repo.resolve(branch.getName())).call();

            for (RevCommit commit : commitsBranchesList) {
                if (!revCommitList.contains(commit)) {
                    revCommitList.add(commit);
                }
            }
        }
        //Sort by date
        revCommitList.sort(Comparator.comparing(o -> o.getCommitterIdent().getWhen()));
        //save info into classes
        for (RevCommit revCommit : revCommitList) {
            Date commitDate = revCommit.getCommitterIdent().getWhen();
            Date lowerBoundDate = new SimpleDateFormat("yyyy-MM-dd").parse("1979-01-01");
            for(Release release : this.releaseList){
                //if lowerBoundDate < commitDate <= releaseDate then the revCommit has been done in that release
                if (commitDate.after(lowerBoundDate) && !commitDate.after(Date.from(release.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    Commit newCommit = new Commit(revCommit, release);
                    commitList.add(newCommit);
                    release.addCommit(newCommit);
                }
                lowerBoundDate = Date.from(release.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

        }
        //remove the release without commit
        releaseList.removeIf(release -> release.getCommitList().isEmpty());
        //order commitList by new date
        commitList.sort(Comparator.comparing(o -> o.getRevCommit().getCommitterIdent().getWhen()));
        return commitList;
    }
    public List<Commit> filterCommits(List<Commit> commitList, List<Ticket> ticketList) {
        this.ticketList = ticketList;
        List<Commit> filteredCommitList = new ArrayList<>();
        for (Commit commit : commitList) {
            String commitFullMessage = commit.getRevCommit().getFullMessage();
            for (Ticket ticket : this.ticketList) {
                String ticketKey = ticket.getTicketKey();
                Pattern pattern = Pattern.compile( ticketKey + "\\b");
                if (pattern.matcher(commitFullMessage).find()) {
                    filteredCommitList.add(commit);
                    ticket.addCommit(commit);
                    commit.setTicket(ticket);
                }
            }
        }
        return filteredCommitList;
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

}
