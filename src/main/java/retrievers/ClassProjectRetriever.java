package retrievers;

import model.ClassProject;
import model.Commit;
import model.Ticket;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClassProjectRetriever {

    private List<Ticket> ticketList;
    int numberOfRelease;

    private String projectName ;
    private Repository repo ;


    private final List<Commit> commitList;

    public ClassProjectRetriever(Repository repository, List<Commit> commitList) {
        this.repo = repository;
        this.commitList = commitList;
        this.numberOfRelease = commitList.size();
    }


    public List<ClassProject> extractAllProjectClasses() throws IOException {
        List<Commit> lastCommitList = new ArrayList<>();
        List<ClassProject> allProjectClasses = new ArrayList<>();

        for(int i = 1; i <= numberOfRelease; i++){
            List<Commit> tempCommits = new ArrayList<>(commitList);
            int j = i;
            tempCommits.removeIf(commit -> (commit.getRelease().id() != j));
            if(tempCommits.isEmpty()){
                continue;
            }
            lastCommitList.add(tempCommits.get(tempCommits.size()-1));
        }
        lastCommitList.sort(Comparator.comparing(o -> o.getRevCommit().getCommitterIdent().getWhen()));

        for(Commit lastCommit: lastCommitList){
            Map<String, String> nameAndContentOfClasses = getClassesMetadata(lastCommit.getRevCommit());
            for(Map.Entry<String, String> nameAndContentOfClass : nameAndContentOfClasses.entrySet()){
                allProjectClasses.add(new ClassProject(nameAndContentOfClass.getKey(), nameAndContentOfClass.getValue(), lastCommit.getRelease()));
            }
        }
        //completeClassesInfo(ticketList, allProjectClasses);
        //keepTrackOfCommitsThatTouchTheClass(allProjectClasses, commitList);
        //allProjectClasses.sort(Comparator.comparing(ProjectClass::getName));
        return allProjectClasses;
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
