package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ticket {
    private final String ticketID;

    private final LocalDate creationDate;
    private final LocalDate resolutionDate;
    private Release injectedVersion;
    private final Release openingVersion;
    private final Release fixedVersion;
    private List<Release> affectedReleases;
    private final List<Commit> commitList;

    /**
     * Constructs a Ticket object with the provided ticket key, creation date, resolution date, opening version, fixed version, and affected releases.
     * If the list of affected releases is empty, the injected version is set to null.
     *
     * @param ticketKey The key of the ticket.
     * @param creationDate The creation date of the ticket.
     * @param resolutionDate The resolution date of the ticket.
     * @param openingVersion The opening version of the ticket.
     * @param fixedVersion The fixed version of the ticket.
     * @param affectedReleases The list of affected releases.
     */

    public Ticket(String ticketKey, LocalDate creationDate, LocalDate resolutionDate, Release openingVersion, Release fixedVersion, List<Release> affectedReleases) {
        this.ticketID = ticketKey;
        this.creationDate = creationDate;
        this.resolutionDate = resolutionDate;
        if(affectedReleases.isEmpty()){
            injectedVersion = null;
        }else{
            injectedVersion = affectedReleases.get(0);
        }
        this.openingVersion = openingVersion;
        this.fixedVersion = fixedVersion;
        this.affectedReleases = affectedReleases;
        commitList = new ArrayList<>();
    }

    public Release getInjectedVersion() {
        return injectedVersion;
    }

    public void setInjectedVersion(Release injectedRelease) {
        this.injectedVersion = injectedRelease;
    }

    public Release getOpeningVersion() {
        return openingVersion;
    }

    public Release getFixedVersion() {
        return fixedVersion;
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
    public static boolean isNotEmpty(Ticket ticket) {
        return !ticket.getAffectedVersions().isEmpty();
    }
    public static List<Ticket> getCorrectTickets(List<Ticket> ticketsList){
        List<Ticket> correctTickets = new ArrayList<>();
        for (Ticket ticket : ticketsList) {
            if (isNotEmpty(ticket)) {
                correctTickets.add(ticket);
            }
        }
        correctTickets.sort(Comparator.comparing(Ticket::getResolutionDate));
        return correctTickets;
    }
    @Override
    public String toString() {
        return "Ticket{" +
                "ticketID='" + ticketID + '\'' +
                ", creationDate=" + creationDate +
                ", resolutionDate=" + resolutionDate +
                ", injectedVersion=" + injectedVersion +
                ", openingVersion=" + openingVersion +
                ", fixedVersion=" + fixedVersion +
                ", affectedVersions=" + affectedReleases +
                '}';
    }


}
