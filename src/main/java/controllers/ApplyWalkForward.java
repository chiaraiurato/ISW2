package controllers;

import exception.ArffFileException;
import exception.CsvFileException;
import model.ClassProject;
import model.Release;
import model.Ticket;
import retrievers.ClassProjectRetriever;
import utilities.ARFF;
import utilities.CSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplyWalkForward {

    private final String projName;
    private final int id;
    private final List<Release> releaseList;
    private final List<Ticket> ticketList;
    private final List<ClassProject> classProjectList;
    private final ClassProjectRetriever classProjectRetriever;

    private List<Release> halfReleases;
    private List<Ticket> halfTickets;
    private List<ClassProject> halfClassProjects;

    public ApplyWalkForward(String projName,List<Release> releaseList, List<Ticket> ticketList, List<ClassProject> classProjectList, ClassProjectRetriever classProjectRetriever) {
        //To avoid snoring discard the most recent release (50%)
        this.id = releaseList.get((releaseList.size() / 2) - 1).id();
        this.projName = projName;
        this.releaseList = releaseList;
        this.ticketList = ticketList;
        this.classProjectList = classProjectList;
        this.classProjectRetriever = classProjectRetriever;
    }

    public void buildTrainingSetAndTestingSet() throws IOException, CsvFileException, ArffFileException {
        for (int i = 1; i <= id; i++) {
            //Training building
            halfReleases = filterReleases(this.releaseList, i);
            halfTickets = filterTickets(this.ticketList, halfReleases);
            halfClassProjects = filterProjectClasses(this.classProjectList, halfReleases);

            classProjectRetriever.injectBuggyClassProjectsForTicketList(halfTickets, halfClassProjects);
            writeTrainingSet(halfReleases,halfClassProjects,i);

            //Testing building
            List<Release> releaseListForTesting = new ArrayList<>();
            for(Release release: releaseList){
                if(release.id() == halfReleases.get(halfReleases.size()-1).id() + 1){
                    releaseListForTesting.add(release);
                    break;
                }
            }
            List<ClassProject> classProjectForTesting = new ArrayList<>(classProjectList);
            //Remove class project entries that have the release id different from the first element of releaseListForTesting
            classProjectForTesting.removeIf(projectClass -> projectClass.getRelease().id() != releaseListForTesting.get(0).id());

            writeTestingSet(releaseListForTesting, classProjectForTesting, i);
        }
    }
    public void writeTrainingSet(List<Release> halfReleases, List<ClassProject> halfClassProjects, int i) throws CsvFileException, ArffFileException {
        ARFF.createFileArff(this.projName,halfReleases,halfClassProjects,"TRAINING-SET",i);
        CSV.createFileCsv(this.projName,halfReleases,halfClassProjects,"TRAINING-SET",i);
    }
    public void writeTestingSet(List<Release> releaseListForTesting, List<ClassProject> classProjectForTesting, int i) throws ArffFileException, CsvFileException {
        ARFF.createFileArff(this.projName,releaseListForTesting,classProjectForTesting,"TESTING-SET",i);
        CSV.createFileCsv(this.projName,releaseListForTesting,classProjectForTesting,"TESTING-SET",i);
    }

    private List<Release> filterReleases(List<Release> releaseList, int threshold) {
        List<Release> filteredReleases = new ArrayList<>(releaseList);
        filteredReleases.removeIf(release -> release.id() > threshold);
        return filteredReleases;
    }

    private List<Ticket> filterTickets(List<Ticket> ticketList, List<Release> filteredReleases) {
        int lastReleaseId = filteredReleases.get(filteredReleases.size() - 1).id();
        List<Ticket> filteredTickets = new ArrayList<>(ticketList);
        filteredTickets.removeIf(ticket -> ticket.getFixedVersion().id() > lastReleaseId);
        return filteredTickets;
    }

    private List<ClassProject> filterProjectClasses(List<ClassProject> projClasses, List<Release> filteredReleases) {
        int lastReleaseId = filteredReleases.get(filteredReleases.size() - 1).id();
        List<ClassProject> filteredProjectClasses = new ArrayList<>(projClasses);
        filteredProjectClasses.removeIf(projectClass -> projectClass.getRelease().id() > lastReleaseId);
        return filteredProjectClasses;
    }

    public List<Release> getHalfReleases() {
        return halfReleases;
    }

    public List<Ticket> getHalfTickets() {
        return halfTickets;
    }

    public List<ClassProject> getHalfClassProjects() {
        return halfClassProjects;
    }
}
