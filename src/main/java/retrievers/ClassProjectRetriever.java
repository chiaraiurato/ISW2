package retrievers;

import model.ClassProject;
import model.Commit;
import model.Release;
import model.Ticket;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class ClassProjectRetriever {

    private final List<Ticket> ticketList;
    int numberOfRelease;

    private final Repository repo ;

    private final List<Commit> commitList;
    /**
     * Constructs a ClassProjectRetriever object for retrieving information about a project's classes.
     *
     * @param repository The Repository object representing the project repository.
     * @param commitList The list of commits associated with the project.
     * @param ticketList The list of tickets associated with the project.
     */
    public ClassProjectRetriever(Repository repository, List<Commit> commitList, List<Ticket> ticketList) {
        this.repo = repository;
        this.commitList = commitList;
        this.numberOfRelease = commitList.size();
        this.ticketList = ticketList;
    }


    public List<ClassProject> extractAllProjectClasses() throws IOException {
        List<Commit> lastCommitList = getLastCommitList();
        List<ClassProject> allProjectClasses = new ArrayList<>();
        lastCommitList.sort(Comparator.comparing(o -> o.getRevCommit().getCommitterIdent().getWhen()));
        for(Commit lastCommit: lastCommitList){
            //Create a map that stores the name of the class along with its content.
            Map<String, String> nameAndContentOfClasses = getClassesMetadata(lastCommit.getRevCommit());
            for(Map.Entry<String, String> nameAndContentOfClass : nameAndContentOfClasses.entrySet()){
                allProjectClasses.add(new ClassProject(nameAndContentOfClass.getKey(), nameAndContentOfClass.getValue(), lastCommit.getRelease()));
            }
        }
        injectBuggyClassProjectsForTicketList(ticketList, allProjectClasses);

        //Store commit that touched a class
        List<ClassProject> tmpClassProj;
        for(Commit commit: commitList){
            Release release = commit.getRelease();
            tmpClassProj = new ArrayList<>(allProjectClasses);
            tmpClassProj.removeIf(tmpClassesProj -> !tmpClassesProj.getRelease().equals(release));
            List<String> modifiedClassesNames = getTouchedClassesNames(commit.getRevCommit());
            for(String modifiedClass: modifiedClassesNames){
                for(ClassProject projectClass: tmpClassProj){
                    if(projectClass.getKey().equals(modifiedClass) && !projectClass.getCommitsOfTheTouchedClass().contains(commit)) {
                        projectClass.addCommitsOfTheTouchedClass(commit);
                    }
                }
            }
        }
        allProjectClasses.sort(Comparator.comparing(ClassProject::getKey));
        return allProjectClasses;
    }
    private List<Commit> getLastCommitList(){
        List<Commit> lastCommitList = new ArrayList<>();
        for (int i = 1; i <= numberOfRelease; i++) {
            List<Commit> tempCommits = new ArrayList<>(commitList);

            int releaseId = i;
            tempCommits.removeIf(commit -> (commit.getRelease().id() != releaseId));
            if (tempCommits.isEmpty()) {
                continue;
            }
            lastCommitList.add(tempCommits.get(tempCommits.size() - 1));
        }
        return lastCommitList;
    }
    public void initializeBuggyness(List<ClassProject> allProjectClasses) throws IOException {
        for(ClassProject projectClass: allProjectClasses ){
            projectClass.getMetric().setBuggyness(false);
        }
    }
    public void injectBuggyClassProjectsForTicketList(List<Ticket> ticketList, List<ClassProject> allProjectClasses) throws IOException {
        initializeBuggyness(allProjectClasses);
        //Set the class as buggy based on the commit and IV
        for(Ticket ticket: ticketList) {
            List<Commit> commitsInsideTicket = ticket.getCommitList();
            Release injectedVersion = ticket.getInjectedVersion();
            if(injectedVersion != null) {
                setClassProjectsBuggy(commitsInsideTicket, injectedVersion, ticket, allProjectClasses);
            }
        }
    }

    public void setClassProjectsBuggy(List<Commit> commitsInsideTicket,Release injectedVersion, Ticket ticket, List<ClassProject> allProjectClasses) throws IOException {
        for (Commit commit : commitsInsideTicket) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            RevCommit revCommit = commit.getRevCommit();
            LocalDate commitDate = LocalDate.parse(formatter.format(revCommit.getCommitterIdent().getWhen()));
            if (!commitDate.isAfter(ticket.getResolutionDate())
                    && !commitDate.isBefore(ticket.getCreationDate())) {
                List<String> modifiedClassesNames = getTouchedClassesNames(revCommit);
                Release fixedVersion = commit.getRelease();
                markAsBuggy(modifiedClassesNames, allProjectClasses, fixedVersion, injectedVersion);
            }
        }
    }
    private void markAsBuggy(List<String> modifiedClassesNames, List<ClassProject> allProjectClasses, Release fixedVersion, Release injectedVersion){
        for (String modifiedClass : modifiedClassesNames) {
            for (ClassProject projectClass : allProjectClasses) {
                if (projectClass.getKey().equals(modifiedClass) && projectClass.getRelease().id() < fixedVersion.id() && projectClass.getRelease().id() >= injectedVersion.id()) {
                    projectClass.getMetric().setBuggyness(true);
                }
            }
        }
    }


    private List<String> getTouchedClassesNames(RevCommit commit) throws IOException {
        List<String> touchedClassesNames = new ArrayList<>();

        // Initialize resources outside try-with-resources

        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE); ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = commit.getTree();
            newTreeIter.reset(reader, newTree);

            RevCommit commitParent = commit.getParent(0);
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = commitParent.getTree();
            oldTreeIter.reset(reader, oldTree);

            diffFormatter.setRepository(repo);
            List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);

            for (DiffEntry entry : entries) {
                if (entry.getNewPath().contains(".java") && !entry.getNewPath().contains("/test/")) {
                    touchedClassesNames.add(entry.getNewPath());
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // Ignore it
        }
        // Close resources in finally block
        return touchedClassesNames;
    }


    private Map<String, String> getClassesMetadata(RevCommit revCommit) throws IOException {
        Map<String, String> allClasses = new HashMap<>();
        RevTree tree = revCommit.getTree();
        TreeWalk treeWalk = new TreeWalk(repo);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        while(treeWalk.next()) {
            if(treeWalk.getPathString().contains(".java") && !treeWalk.getPathString().contains("/test/")) {
                allClasses.put(treeWalk.getPathString(), new String(repo.open(treeWalk.getObjectId(0)).getBytes(), StandardCharsets.UTF_8));
            }
        }
        treeWalk.close();
        return allClasses;
    }
}
