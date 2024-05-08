package org.uniroma2;

import model.Commit;
import model.Release;
import model.Ticket;
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
    private static final String DONE = "Done!";
    private Execute() {
    }

    public static void collectData(String projName, String projURL) throws IOException, URISyntaxException, GitAPIException, ParseException {
        //Get first all releases
        logger.info("----------" + projName.toUpperCase() + "----------");
        logger.info("Retrieving releases...");
        ReleaseRetriever releaseRetriever = new ReleaseRetriever(projName);
        List<Release> releaseList = releaseRetriever.getVersions(projName);
        logger.info(DONE);

        logger.info("Number of releases " + releaseList.size());
        logger.info("Retrieving commit...");
        CommitRetriever commitRetriever = new CommitRetriever(projName, projURL, releaseList);
        List<Commit> commitList = commitRetriever.extractAllCommits();

        logger.info("Number of commit " + commitList.size());
        logger.info(DONE);
        logger.info("Retrieving tickets...");
        TicketRetriever ticketRetriever = new TicketRetriever(projName, releaseList);
        List<Ticket> ticketList = ticketRetriever.getBugTickets();
        logger.info("Number of ticket " + ticketList.size());
        logger.info(DONE);


    }


}
