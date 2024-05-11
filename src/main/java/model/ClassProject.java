package model;

import java.util.ArrayList;
import java.util.List;
public class ClassProject {
    private final String key;

    private final String value;
    private final Release release;
    private final Metric metric;
    private final List<Commit> commitsOfTheTouchTheClass;
    //private final List<Integer> lOCAddedByClass;
    //private final List<Integer> lOCRemovedByClass;

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
        this.commitsOfTheTouchTheClass = new ArrayList<>();
        this.metric = new Metric();
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

    public List<Commit> getCommitsThatTouchTheClass() {
        return commitsOfTheTouchTheClass;
    }
    public void addCommitOfTheTouchedClass(Commit commit) {
        this.commitsOfTheTouchTheClass.add(commit);
    }

    public Metric getMetric() {
        return metric;
    }
}
