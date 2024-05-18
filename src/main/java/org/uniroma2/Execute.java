package org.uniroma2;

import controllers.WalkForwardController;
import model.*;
import controllers.MetricsController;
import controllers.SetRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.LoggerFactory;
import retrievers.*;
import org.slf4j.Logger;
import utilities.CSV;
import utilities.TXT;

import java.util.List;

public class Execute {
    private static final Logger logger = LoggerFactory.getLogger(Execute.class);
    private static final String DONE = "Done!";
    private Execute() {
    }

    public static void collectData(String projName, String projURL) throws Exception {
        //Set the directory and clone the repo if not exists
        SetRepository setRepository = new SetRepository(projName,projURL);
        Repository repository = setRepository.getRepo();
        Git git = setRepository.getGit();

        //Create the instance for report
        TXT createReportFile = new TXT(projName);

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
        MetricsController metricsController = new MetricsController(classProjects, commitBuggy, repository);
        metricsController.calculateAllMetrics();
        logger.info(DONE);

        //Summarize all info into report files
        createReportFile.begin(releaseList, commitList, ticketList, commitBuggy);

        //To avoid snoring discard the most recent release (50%)
        int half_size = (releaseList.size() / 2);

        //Starting Walk-Forward approach
        logger.info("Building training set and testing set...");
        WalkForwardController walkForwardController = new WalkForwardController(projName, half_size,releaseList, ticketList,classProjects,classProjectRetriever);
        walkForwardController.buildTrainingSetAndTestingSet();
        logger.info(DONE);

        //Now that the dataset is ready we can do ML
        logger.info("Do some magic prediction...");
        WekaRetriever wekaRetriever = new WekaRetriever(projName,half_size);
        List<ResultOfClassifier> resultsOfClassifierList= wekaRetriever.computeAllClassifier();
        logger.info(DONE);

        //Write a final csv
        logger.info("Printing out info...");
        CSV.createFinalCsv(projName,resultsOfClassifierList);

    }




}
