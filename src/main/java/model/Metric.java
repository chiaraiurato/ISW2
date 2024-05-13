package model;

public class Metric{
    //Metrics for RemovedLOC
    private int maxRemovedLOCMetrics;
    private float avgRemovedLOCMetrics;
    private int removedLOCMetrics;
    //Metrics for Churn
    private int churnMetrics;
    private float avgChurnMetrics;
    private int maxChurnMetrics;
    //Metrics for AddedLOC
    private int addedLOCMetrics;
    private int maxAddedLOCMetrics;
    private float avgAddedLOCMetrics;
    //Metrics for touchedLOC
    private int touchedLOCMetrics;
    private int maxTouchedLOCMetrics;
    private float avgTouchedLOCMetrics;

    private boolean bugged;
    private int size;
    private int numberOfRevisions;
    private int numberOfDefectFixes;
    private int numberOfAuthors;

    /**
     * Metric class represents metrics associated with a software project.
     * It includes metrics such as lines of code added, removed, churn, and touched, as well as other project-related metrics.
     * The class provides methods to initialize, set, and retrieve these metrics.
     * Metrics include:
     * - Removed Lines of Code (LOC) metrics: total removed LOC, maximum removed LOC, and average removed LOC.
     * - Churn metrics: total churn, maximum churn, and average churn.
     * - Added Lines of Code (LOC) metrics: total added LOC, maximum added LOC, and average added LOC.
     * - Touched Lines of Code (LOC) metrics: total touched LOC, maximum touched LOC, and average touched LOC.
     * - Project-related metrics: buggyness, size, number of revisions, number of defect fixes, and number of authors.
     * Additionally, the class overrides the toString() method to provide a string representation of the metrics.
     */

    public Metric() {
        initializeMetrics();
    }

    private void initializeMetrics() {
        bugged = false;
        size = 0;
        numberOfRevisions = 0;
        numberOfDefectFixes = 0;
        numberOfAuthors = 0;
        removedLOCMetrics = 0;
        churnMetrics = 0;
        addedLOCMetrics = 0;
        touchedLOCMetrics = 0;
    }

    public boolean getBuggyness() {
        return bugged;
    }

    public void setBuggyness(boolean bugged) {
        this.bugged = bugged;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSize() {
        return String.valueOf(size);
    }

    public String getRemovedLOCMetrics() {
        return String.valueOf(removedLOCMetrics);
    }

    public String getChurnMetrics() {
        return String.valueOf(churnMetrics);
    }

    public String getAddedLOCMetrics() {
        return String.valueOf(addedLOCMetrics);
    }

    public String getTouchedLOCMetrics() {
        return String.valueOf(touchedLOCMetrics);
    }


    public void setNumberOfRevisions(int numberOfRevisions) {
        this.numberOfRevisions = numberOfRevisions;
    }

    public void setNumberOfDefectFixes(int numberOfDefectFixes) {
        this.numberOfDefectFixes = numberOfDefectFixes;
    }

    public int getNumberOfDefectFixes() {
        return numberOfDefectFixes;
    }
    public String getStringNumberOfDefectFixes() {
        return String.valueOf(numberOfDefectFixes);
    }

    public void setNumberOfAuthors(int numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }

    public int getNumberOfAuthors() {
        return numberOfAuthors;
    }
    public String getStringNumberOfAuthors() {
        return String.valueOf(numberOfAuthors);
    }

    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }
    public String getStringNumberOfRevisions() {
        return String.valueOf(numberOfRevisions);
    }

    public void setRemovedLOCMetrics(int removedLOCMetrics, int maxRemovedLOCMetrics, float avgRemovedLOCMetrics) {
        this.removedLOCMetrics = removedLOCMetrics;
        this.maxRemovedLOCMetrics = maxRemovedLOCMetrics;
        this.avgRemovedLOCMetrics= avgRemovedLOCMetrics;
    }

    public void setChurnMetrics(int churnMetrics, int maxChurnMetrics, float avgChurnMetrics) {
        this.churnMetrics = churnMetrics;
        this.maxChurnMetrics = maxChurnMetrics;
        this.avgChurnMetrics = avgChurnMetrics;
    }

    public void setTouchedLOCMetrics(int touchedLOCMetrics,int maxTouchedLOCMetrics, float avgTouchedLOCMetrics) {
        this.touchedLOCMetrics = touchedLOCMetrics;
        this.maxTouchedLOCMetrics = maxTouchedLOCMetrics;
        this.avgTouchedLOCMetrics = avgTouchedLOCMetrics;
    }

    public void setAddedLOCMetrics(int addedLOCMetrics, int maxAddedLOCMetrics, float avgAddedLOCMetrics) {
        this.addedLOCMetrics = addedLOCMetrics;
        this.avgAddedLOCMetrics=avgAddedLOCMetrics;
        this.maxAddedLOCMetrics = maxAddedLOCMetrics;
    }
    public String getMaxRemovedLOCMetrics() {
        return String.valueOf(maxRemovedLOCMetrics);
    }
    public String getAvgRemovedLOCMetrics() {
        return String.valueOf(avgRemovedLOCMetrics);
    }

    public String getAvgChurnMetrics() {
        return String.valueOf(avgChurnMetrics);
    }

    public String getMaxChurnMetrics() {
        return String.valueOf(maxChurnMetrics);
    }

    public String getMaxAddedLOCMetrics() {
        return String.valueOf(maxAddedLOCMetrics);
    }

    public String getAvgAddedLOCMetrics() {
        return String.valueOf(avgAddedLOCMetrics);
    }

    public String getMaxTouchedLOCMetrics() {
        return String.valueOf(maxTouchedLOCMetrics);
    }

    public String getAvgTouchedLOCMetrics() {
        return String.valueOf(avgTouchedLOCMetrics);
    }

    @Override
    public String toString() {
        return "Metric{" +
                "maxRemovedLOCMetrics=" + maxRemovedLOCMetrics +
                ", avgRemovedLOCMetrics=" + avgRemovedLOCMetrics +
                ", removedLOCMetrics=" + removedLOCMetrics +
                ", churnMetrics=" + churnMetrics +
                ", addedLOCMetrics=" + addedLOCMetrics +
                ", maxAddedLOCMetrics=" + maxAddedLOCMetrics +
                ", avgAddedLOCMetrics=" + avgAddedLOCMetrics +
                ", touchedLOCMetrics=" + touchedLOCMetrics +
                ", bugged=" + bugged +
                ", size=" + size +
                ", numberOfRevisions=" + numberOfRevisions +
                ", numberOfDefectFixes=" + numberOfDefectFixes +
                ", numberOfAuthors=" + numberOfAuthors +
                '}';
    }


}
