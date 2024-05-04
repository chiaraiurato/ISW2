package org.example;

import model.Commit;
import model.Version;
import org.eclipse.jgit.api.errors.GitAPIException;
import retrievers.CommitRetriever;
import retrievers.TicketRetriever;
import retrievers.VersionRetriever;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

public class Execute {

    private Execute() {
    }

    public static void collectData(String projName) throws IOException, URISyntaxException, GitAPIException, ParseException {
        //Get first all releases
        System.out.println("Retrieving releases...");
        VersionRetriever versionRetriever = new VersionRetriever(projName);
        List<Version> versionList = versionRetriever.getVersions(projName);
        System.out.println("Done!");
        System.out.println("Retrieving commit...");
        CommitRetriever commitRetriever = new CommitRetriever(projName, "https://github.com/chiaraiurato/bookkeeper.git", versionList);
        List<Commit> commitList = commitRetriever.extractAllCommits();
        System.out.println("Done!");
        System.out.println("Retrieving tickets...");
        TicketRetriever ticketRetriever = new TicketRetriever(projName, versionList);
        ticketRetriever.getBugTickets();
        System.out.println("Done!");


    }

//    private static @NotNull List<ReleaseInfo> discardHalfReleases(@NotNull List<ReleaseInfo> releaseInfoList) {
//
//        int n = releaseInfoList.size();
//
//        releaseInfoList.sort((o1, o2) -> {
//            Integer i1 = o1.getRelease().getIndex();
//            Integer i2 = o2.getRelease().getIndex();
//            return i1.compareTo(i2);
//        });
//
//        return releaseInfoList.subList(0, n/2+1);
//    }
}
