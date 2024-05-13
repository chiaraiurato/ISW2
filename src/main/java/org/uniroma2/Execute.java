package org.uniroma2;

import model.ClassProject;
import model.Commit;
import model.Release;
import model.Ticket;
import controllers.CalculateMetrics;
import controllers.SetRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.LoggerFactory;
import retrievers.ClassProjectRetriever;
import retrievers.CommitRetriever;
import retrievers.TicketRetriever;
import retrievers.ReleaseRetriever;
import org.slf4j.Logger;
import utilities.CreateReportFile;
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
        //Set the directory and clone the repo if not exists
        SetRepository setRepository = new SetRepository(projName,projURL);
        Repository repository = setRepository.getRepo();
        Git git = setRepository.getGit();

        //Create the instance for report
        CreateReportFile createReportFile = new CreateReportFile(projName);

        logger.info(String.format("---------- %s ----------", projName));
        //Retrieve release
        logger.info("Retrieving releases...");
        ReleaseRetriever releaseRetriever = new ReleaseRetriever(projName);
        List<Release> releaseList = releaseRetriever.getVersions();
        logger.info(DONE);

        //Retrieve commit
        logger.info("Retrieving commit...");
        CommitRetriever commitRetriever = new CommitRetriever(git,repository, releaseList);
        List<Commit> commitList = commitRetriever.extractAllCommits();
        releaseList = commitRetriever.getReleaseList();
        logger.info(DONE);

        //Retrieve ticket
        logger.info("Retrieving tickets...");
        TicketRetriever ticketRetriever = new TicketRetriever(projName, releaseList);
        List<Ticket> ticketList = ticketRetriever.getTickets();
        ticketList = ticketRetriever.doProportion(ticketList, releaseList);
        logger.info(DONE);

        //Filter commit that have ticket id inside their message
        logger.info("Filtering commits that contains bug...");
        List<Commit> commitBuggy = commitRetriever.filterBuggyCommits(commitList, ticketList);
        ticketList = commitRetriever.getTicketList();
        logger.info(DONE);

        //Now we can retrieve the touched classes
        logger.info("Retrieving class project...");
        ClassProjectRetriever classProjectRetriever = new ClassProjectRetriever(repository, commitList, ticketList);
        List<ClassProject> classProjects = classProjectRetriever.extractAllProjectClasses();
        logger.info(DONE);

        //Calculate metrics
        logger.info("Calculating metrics...");
        CalculateMetrics calculateMetrics = new CalculateMetrics(classProjects, commitBuggy, repository);
        calculateMetrics.calculateAllMetrics();
        logger.info(DONE);

        //Summarize all info into report files
        createReportFile.begin(releaseList, commitList, ticketList, commitBuggy);

    }



}
