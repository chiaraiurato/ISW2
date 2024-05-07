package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private final String ticketID;

    private final LocalDate creationDate;
    private final LocalDate resolutionDate;
    private Release injectedRelease;
    private final Release openingRelease;
    private final Release fixedRelease;
    private List<Release> affectedReleases;
    private final List<Commit> commitList;

    public Ticket(String ticketKey, LocalDate creationDate, LocalDate resolutionDate, Release openingRelease, Release fixedRelease, List<Release> affectedReleases) {
        this.ticketID = ticketKey;
        this.creationDate = creationDate;
        this.resolutionDate = resolutionDate;
        if(affectedReleases.isEmpty()){
            injectedRelease = null;
        }else{
            injectedRelease = affectedReleases.get(0);
        }
        this.openingRelease = openingRelease;
        this.fixedRelease = fixedRelease;
        this.affectedReleases = affectedReleases;
        commitList = new ArrayList<>();
    }

    public Release getInjectedVersion() {
        return injectedRelease;
    }

    public void setInjectedVersion(Release injectedRelease) {
        this.injectedRelease = injectedRelease;
    }

    public Release getOpeningVersion() {
        return openingRelease;
    }

    public Release getFixedVersion() {
        return fixedRelease;
    }

    public List<Release> getAffectedVersions() {
        return affectedReleases;
    }

    public void setAffectedVersions(List<Release> affectedReleases) {
        this.affectedReleases = affectedReleases;
    }

    public String getTicketKey() {
        return ticketID;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void addCommit(Commit newCommit) {
        if(!commitList.contains(newCommit)){
            commitList.add(newCommit);
        }
    }

    public List<Commit> getCommitList(){
        return commitList;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }
}
