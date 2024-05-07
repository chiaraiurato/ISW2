package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Release {
    private Integer id;
    private final String releaseName;
    private final LocalDate releaseDate;
    private final List<Commit> commitList;

    public Release(String releaseName, LocalDate releaseDate) {
        this.releaseName = releaseName;
        this.releaseDate = releaseDate;
        commitList = new ArrayList<>();
    }

    public Release(int id, String releaseName, LocalDate releaseDate) {
        this.id = id;
        this.releaseName = releaseName;
        this.releaseDate = releaseDate;
        commitList = new ArrayList<>();
    }

    public int id() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String releaseName() {
        return releaseName;
    }

    public LocalDate releaseDate() {
        return releaseDate;
    }

    public void addCommit(Commit newCommit) {
        if(!commitList.contains(newCommit)){
            commitList.add(newCommit);
        }
    }

    public List<Commit> getCommitList(){
        return commitList;
    }
}
