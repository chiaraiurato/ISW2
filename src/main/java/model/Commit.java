package model;

import org.eclipse.jgit.revwalk.RevCommit;

public class Commit {
    private final RevCommit revCommit;
    private Ticket ticket;
    private final Version version;

    public Commit(RevCommit revCommit, Version version) {
        this.revCommit = revCommit;
        this.version = version;
        ticket = null;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Version getVersion() {
        return version;
    }
}
