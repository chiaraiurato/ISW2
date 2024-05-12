package controllers;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;

public class SetRepository {

    private final Repository repo ;
    private final Git git;
    /**
     * Controller class for setting up and accessing the repository and Git instance for a project.
     * This class allows for the creation of a repository and Git instance based on the project name and URL.
     * If the repository directory already exists locally, it initializes the repository and Git instance.
     * Otherwise, it clones the repository from the provided URL into a new directory.
     * Provides methods to retrieve the repository and Git instances.
     * @param projName The name of the project
     * @param projURL  The project URL
     */

    public SetRepository(String projName, String projURL) throws IOException, GitAPIException {

        File directory = new File("temp/" + projName); //Create the directory where to clone repo
        if (directory.exists()) {
            this.repo = new FileRepository("temp/" + projName + "/.git");
            this.git = new Git(this.repo);
        }else{
        git = Git.cloneRepository().setURI(projURL).setDirectory(directory).call();
        repo = git.getRepository();
    }
    }
    public Repository getRepo() {
        return repo;
    }

    public Git getGit() {
        return git;
    }


}
