package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import model.ClassProject;
import model.Commit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class MetricsController {
    private final List<ClassProject> allClasses;
    private final List<Commit> filteredCommits;
    private final Repository repo;

    /**
     * Constructs a CalculateMetrics object with the given lists of class projects and filtered commits.
     *
     * @param classProjects   The list of class projects to calculate metrics for.
     * @param filteredCommits The list of filtered commits to be used for metric calculation.
     * @param repository The repo where to extract LOC.
     */
    public MetricsController(List<ClassProject> classProjects, List<Commit> filteredCommits, Repository repository) {
        this.allClasses = classProjects;
        this.filteredCommits = filteredCommits;
        this.repo = repository;
    }

    public void calculateAllMetrics() throws IOException {
        //The Lines Of Code (LOC)
        calculateSize();
        //The number of revision
        calculateNR();
        //The Number of bug fixes
        calculateNFix();
        //The Number of Authors
        calculateNAuth();
        //The addedLOC, removedLOC, churn, touchedLOC
        calculateLOCMetrics();
    }
    public void calculateLOCMetrics() throws IOException {
        for (ClassProject classProject : allClasses) {
            //Initialize metrics
            int totalAdded = 0;
            int totalRemoved = 0;
            int totalChurn = 0;
            int totalTouched = 0;

            int maxAdded = 0;
            int maxRemoved = 0;
            int maxChurn = 0;
            int maxTouched = 0;

            // Extract LOC from git
            extractAddedOrRemovedLOC(classProject);

            List<Integer> locAddedByClass = classProject.getLOCAddedByClass();
            List<Integer> locRemovedByClass = classProject.getLOCRemovedByClass();

            for (int i = 0; i < locAddedByClass.size(); i++) {
                totalAdded += locAddedByClass.get(i);
                totalRemoved += locRemovedByClass.get(i);
                totalChurn += Math.abs(locAddedByClass.get(i) - locRemovedByClass.get(i));
                totalTouched += locAddedByClass.get(i) + locRemovedByClass.get(i);

                maxAdded = Math.max(maxAdded, totalAdded);
                maxRemoved = Math.max(maxRemoved, totalRemoved);
                maxChurn = Math.max(maxChurn, totalChurn);
                maxTouched = Math.max(maxTouched, totalTouched);
            }

            int nRevisions = classProject.getMetric().getNumberOfRevisions();
            if (nRevisions > 0) {
                setMetricsForRemovedLOC(nRevisions,totalRemoved, maxRemoved, classProject);
                setMetricsForAddedLOC(nRevisions, totalAdded, maxAdded, classProject);
                setMetricsForTouchedLoc(nRevisions, totalTouched, maxTouched, classProject);
                setMetricsForChurn(nRevisions,totalChurn, maxChurn,classProject);
            }

        }
    }

    private void setMetricsForChurn(int nRevisions, int totalChurn, int maxChurn, ClassProject classProject) {
        float avgChurn = (float) totalChurn / nRevisions;
        classProject.getMetric().setChurnMetrics(totalChurn,maxChurn,avgChurn);
    }

    private void setMetricsForTouchedLoc(int nRevisions, int totalTouched, int maxTouched, ClassProject classProject) {
        float avgTouched = ((float) totalTouched) / nRevisions;
        classProject.getMetric().setTouchedLOCMetrics(totalTouched,maxTouched,avgTouched);
    }

    private void setMetricsForAddedLOC(int nRevisions, int totalAdded, int maxAdded, ClassProject classProject) {
        float avgAdded = ((float) totalAdded) / nRevisions;
        classProject.getMetric().setAddedLOCMetrics(totalAdded, maxAdded, avgAdded);
    }

    private void setMetricsForRemovedLOC(int nRevisions, int totalRemoved, int maxRemoved, ClassProject classProject) {

        float avgRemoved = ((float) totalRemoved) / nRevisions;

        classProject.getMetric().setRemovedLOCMetrics(totalRemoved, maxRemoved, avgRemoved);
    }


    public void extractAddedOrRemovedLOC(ClassProject classProject) throws IOException {
        for(Commit commit : classProject.getCommitsOfTheTouchedClass()) {
            RevCommit revCommit = commit.getRevCommit();
            try(DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
                RevCommit parentComm = revCommit.getParent(0);
                diffFormatter.setRepository(repo);
                diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
                List<DiffEntry> diffEntries = diffFormatter.scan(parentComm.getTree(), revCommit.getTree());
                for(DiffEntry diffEntry : diffEntries) {
                    if(diffEntry.getNewPath().equals(classProject.getKey())) {
                        classProject.addLOCAddedByClass(calculateAddedLines(diffFormatter, diffEntry));
                        classProject.addLOCRemovedByClass(calculateDeletedLines(diffFormatter, diffEntry));
                    }
                }
            } catch(ArrayIndexOutOfBoundsException ignored) {
                //ignoring it
            }
        }
    }

    /**
     * Calculates the number of added lines for a specific diff entry.
     *
     * @param diffFormatter The diff formatter used to get the changes.
     * @param entry         The diff entry for which to calculate the added lines.
     * @return The number of added lines.
     * @throws IOException If an I/O error occurs while calculating the added lines.
     */
    private int calculateAddedLines(DiffFormatter diffFormatter, DiffEntry entry) throws IOException {
        int addedLines = 0;
        for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
            addedLines += edit.getEndB() - edit.getBeginB();
        }
        return addedLines;
    }

    /**
     * Calculates the number of deleted lines for a specific diff entry.
     *
     * @param diffFormatter The diff formatter used to get the changes.
     * @param entry         The diff entry for which to calculate the deleted lines.
     * @return The number of deleted lines.
     * @throws IOException If an I/O error occurs while calculating the deleted lines.
     */
    private int calculateDeletedLines(DiffFormatter diffFormatter, DiffEntry entry) throws IOException {
        int deletedLines = 0;
        for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
            deletedLines += edit.getEndA() - edit.getBeginA();
        }
        return deletedLines;
    }

    private void calculateNAuth(){
         for(ClassProject classProject : allClasses) {
            List<String> authors = new ArrayList<>();
            for(Commit commit : classProject.getCommitsOfTheTouchedClass()) {
                RevCommit revCommit = commit.getRevCommit();
                if(!authors.contains(revCommit.getAuthorIdent().getName())) {
                    authors.add(revCommit.getAuthorIdent().getName());
                }
            }
            classProject.getMetric().setNumberOfAuthors(authors.size());
        }
    }

    private void calculateNFix(){
        int nFix;
        for(ClassProject classProject : allClasses) {
            nFix = 0;
            for(Commit commitThatTouchesTheClass: classProject.getCommitsOfTheTouchedClass()) {
                if (filteredCommits.contains(commitThatTouchesTheClass)) {
                    nFix++;
                }
            }
            classProject.getMetric().setNumberOfDefectFixes(nFix);
        }
    }

    private void calculateNR() {
        for(ClassProject classProject : allClasses) {
            classProject.getMetric().setNumberOfRevisions(classProject.getCommitsOfTheTouchedClass().size());
        }
    }

    private void calculateSize() {
        for(ClassProject classProject : allClasses) {
            String[] lines = classProject.getValue().split("\r\n|\r|\n");
            classProject.getMetric().setSize(lines.length);
        }
    }


}
