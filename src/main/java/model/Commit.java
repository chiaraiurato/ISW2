package model;

import org.eclipse.jgit.revwalk.RevCommit;

public class Commit {
    private final RevCommit revCommit;
    private Ticket ticket;
    private final Release release;

    public Commit(RevCommit revCommit, Release release) {
        this.revCommit = revCommit;
        this.release = release;
        this.ticket = null;
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

    public Release getVersion() {
        return release;
    }

    public Release getRelease() {
        return release;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "revCommit=" + revCommit +
                ", ticket=" + ticket +
                ", release=" + release +
                '}';
    }

}
