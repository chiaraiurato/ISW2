package model;

import org.eclipse.jgit.revwalk.RevCommit;

public class Commit {
    private final RevCommit revCommit;
    private Ticket ticket;
    private final Release release;

    /**
     * Constructs a Commit object with the specified RevCommit and Release.
     * This constructor initializes the Commit object with the provided RevCommit and Release instances.
     * The ticket associated with the commit is set to null.
     *
     * @param revCommit The RevCommit associated with the commit.
     * @param release   The Release associated with the commit.
     */
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
