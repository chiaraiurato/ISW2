package org.example;

import model.Commit;
import model.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.LoggerFactory;
import retrievers.CommitRetriever;
import retrievers.TicketRetriever;
import retrievers.ReleaseRetriever;
import org.slf4j.Logger;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

public class Execute {
    private static final Logger logger = LoggerFactory.getLogger(Execute.class);
    private Execute() {
    }

    public static void collectData(String projName, String projURL) throws IOException, URISyntaxException, GitAPIException, ParseException {
        //Get first all releases
        logger.info("Retrieving releases...");
        ReleaseRetriever releaseRetriever = new ReleaseRetriever(projName);
        List<Release> releaseList = releaseRetriever.getVersions(projName);
        logger.info("Done!");
        logger.info("Retrieving commit...");
        CommitRetriever commitRetriever = new CommitRetriever(projName, projURL, releaseList);
        List<Commit> commitList = commitRetriever.extractAllCommits();
        logger.info("Done!");
        logger.info("Retrieving tickets...");
        TicketRetriever ticketRetriever = new TicketRetriever(projName, releaseList);
        ticketRetriever.getBugTickets();
        logger.info("Done!");


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
