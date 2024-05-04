package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private final String ticketID;

    private final LocalDate creationDate;
    private final LocalDate resolutionDate;
    private Version injectedVersion;
    private final Version openingVersion;
    private final Version fixedVersion;
    private List<Version> affectedVersions;
    private final List<Commit> commitList;

    public Ticket(String ticketKey, LocalDate creationDate, LocalDate resolutionDate, Version openingVersion, Version fixedVersion, List<Version> affectedVersions) {
        this.ticketID = ticketKey;
        this.creationDate = creationDate;
        this.resolutionDate = resolutionDate;
        if(affectedVersions.isEmpty()){
            injectedVersion = null;
        }else{
            injectedVersion = affectedVersions.get(0);
        }
        this.openingVersion = openingVersion;
        this.fixedVersion = fixedVersion;
        this.affectedVersions = affectedVersions;
        commitList = new ArrayList<>();
    }

    public Version getInjectedVersion() {
        return injectedVersion;
    }

    public void setInjectedVersion(Version injectedVersion) {
        this.injectedVersion = injectedVersion;
    }

    public Version getOpeningVersion() {
        return openingVersion;
    }

    public Version getFixedVersion() {
        return fixedVersion;
    }

    public List<Version> getAffectedVersions() {
        return affectedVersions;
    }

    public void setAffectedVersions(List<Version> affectedVersions) {
        this.affectedVersions = affectedVersions;
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
