package controllers;

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

    public void buildTrainingSet() throws IOException {
        for (int i = 1; i <= id; i++) {
            halfReleases = filterReleases(this.releaseList, i);
            halfTickets = filterTickets(this.ticketList, halfReleases);
            halfClassProjects = filterProjectClasses(this.classProjectList, halfReleases);

            classProjectRetriever.injectBuggyClassProjectsForTicketList(halfTickets, halfClassProjects);
            ARFF.createFileArff(projName,halfReleases,halfClassProjects,"TRAINING-SET",i);
            CSV.createFileCsv(projName,halfReleases,halfClassProjects,"TRAINING-SET",i);
        }
    }
    public void buildTestingSet(){
        List<Release> testingSetReleaseList = new ArrayList<>();
        for(Release release: releaseList){
            if(release.id() == halfReleases.get(halfReleases.size()-1).id() + 1){
                testingSetReleaseList.add(release);
                break;
            }
        }
        List<ClassProject> firstIProjectClassesTesting = new ArrayList<>(classProjectList);
        firstIProjectClassesTesting.removeIf(projectClass -> projectClass.getRelease().id() != testingSetReleaseList.get(0).id());
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
