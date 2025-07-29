package com.cicd.generator.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.nio.file.Path;

public class GitService {
    public void commitAndPush(Path repoPath, String message, String username, String password)
            throws Exception {

        try (Git git = Git.open(repoPath.toFile())) {
            // Stage all changes
            git.add().addFilepattern(".").call();

            // Commit changes
            git.commit()
                    .setMessage(message)
                    .call();

            // Push changes
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .call();
        }
    }
}