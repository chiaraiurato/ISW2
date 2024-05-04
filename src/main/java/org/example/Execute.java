package org.example;

import retrivers.TicketRetriever;
import retrivers.VersionRetriever;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class Execute {
    private static final Logger logger = Logger.getLogger(Execute.class.getName());


    private Execute() {
    }

    public static void collectData(String projName) throws IOException, URISyntaxException {

        TicketRetriever ticketRetriever = new TicketRetriever(projName);
        ticketRetriever.retrieveBugTickets();
        //CommitRetriever commitRetriever = ticketRetriever.getCommitRetriever();
        VersionRetriever versionRetriever = new VersionRetriever(projName);
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
