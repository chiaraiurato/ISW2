package model;

import java.util.ArrayList;
import java.util.List;
public class ClassProject {
    private final String key;
    private final String value;
    private final Release release;
    private final Metric metric;
    private final List<Commit> commitsOfTheTouchedClass;
    private final List<Integer> lOCAddedByClass;
    private final List<Integer> lOCRemovedByClass;

    /**
     * This is the constructor that you have to use for retrieve commits.
     *
     * @param key The name of the Java Class.
     * @param value The content of the class
     * @param release The release.
     */

    public ClassProject(String key, String value, Release release) {
        this.key = key;
        this.value = value;
        this.release = release;
        this.metric = new Metric();
        this.commitsOfTheTouchedClass = new ArrayList<>();
        this.lOCAddedByClass = new ArrayList<>();
        this.lOCRemovedByClass = new ArrayList<>();
    }
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Release getRelease() {
        return release;
    }

    public List<Commit> getCommitsOfTheTouchedClass() {
        return commitsOfTheTouchedClass;
    }
    public void addCommitsOfTheTouchedClass(Commit commit) {
        this.commitsOfTheTouchedClass.add(commit);
    }

    public Metric getMetric() {
        return metric;
    }

    public List<Integer> getLOCAddedByClass() {
        return lOCAddedByClass;
    }

    public void addLOCAddedByClass(Integer lOCAddedByEntry) {
        lOCAddedByClass.add(lOCAddedByEntry);
    }

    public List<Integer> getLOCRemovedByClass() {
        return lOCRemovedByClass;
    }

    public void addLOCRemovedByClass(Integer lOCRemovedByEntry) {
        lOCRemovedByClass.add(lOCRemovedByEntry);
    }

    @Override
    public String toString() {
        return "ClassProject{" +
                "key='" + key + '\'' +
                ", release=" + release +
                ", metric=" + metric +
                ", commitsOfTheTouchedClass=" + commitsOfTheTouchedClass +
                ", lOCAddedByClass=" + lOCAddedByClass +
                ", lOCRemovedByClass=" + lOCRemovedByClass +
                '}';
    }
}
