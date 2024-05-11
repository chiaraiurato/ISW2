package org.uniroma2;

import model.ClassProject;
import model.Commit;
import model.Release;
import model.Ticket;
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

        logger.info("----------" + projName + "----------");
        //Retrieve release
        logger.info("Retrieving releases...");
        ReleaseRetriever releaseRetriever = new ReleaseRetriever(projName);
        List<Release> releaseList = releaseRetriever.getVersions(projName);
        printListHead(releaseList, 3);
        logger.info(DONE);
        logger.info("Number of releases " + releaseList.size());

        //Retrieve commit
        logger.info("Retrieving commit...");
        CommitRetriever commitRetriever = new CommitRetriever(git,repository, releaseList);
        List<Commit> commitList = commitRetriever.extractAllCommits();
        logger.info("Number of commit " + commitList.size());
        logger.info(DONE);

        //Retrieve ticket
        logger.info("Retrieving tickets...");
        TicketRetriever ticketRetriever = new TicketRetriever(projName, releaseList);
        List<Ticket> ticketList = ticketRetriever.getTickets();
        logger.info("Number of ticket " + ticketList.size());
        logger.info(DONE);

        //Filter commit that have ticket id inside their message
        logger.info("Filtering commits...");
        List<Commit> filterCommits = commitRetriever.filterCommits(commitList, ticketList);
        logger.info(DONE);

        //Now we can retrieve the touched classes
        logger.info("Retrieving class project...");
        ClassProjectRetriever classProjectRetriever = new ClassProjectRetriever(repository, commitList, ticketList);
        List<ClassProject> classProjects = classProjectRetriever.extractAllProjectClasses();
        logger.info("Number of class  " + classProjects.size());
        logger.info(DONE);

    }

    private static <T> void printListHead(List<T> list, int numRows) {
        logger.info("Printing head of list:");
        for (int i = 0; i < Math.min(numRows, list.size()); i++) {
            logger.info(list.get(i).toString());
        }
    }



}
