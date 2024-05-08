package model.controllers;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;

public class SetRepository {

    private final Repository repo ;
    private final Git git;
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
