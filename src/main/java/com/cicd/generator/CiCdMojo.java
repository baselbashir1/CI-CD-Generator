package com.cicd.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;

@Mojo(name = "cicd")
public class CiCdMojo extends AbstractMojo {

    @Parameter
    private MavenProject project;

    @Parameter
    private String imageName;

    @Parameter
    private String containerName;

    @Parameter
    private String portMapping;

    @Parameter
    private String baseImage;

    @Parameter
    private String contextPath;

    public void execute() throws MojoExecutionException {
        try {
            createDockerfile();
            buildDockerImage();
            runDockerContainer();
        } catch (Exception e) {
            throw new MojoExecutionException("CI/CD failed", e);
        }
    }

    private void createDockerfile() throws Exception {
        getLog().info("Creating Dockerfile");
        File dockerfile = new File(project.getBasedir(), "Dockerfile");
        try (PrintWriter writer = new PrintWriter(dockerfile)) {
            writer.println("FROM " + baseImage);
            writer.println();
            writer.println("WORKDIR /app");
            writer.println();
            String finalName = project.getBuild().getFinalName() + "." + project.getPackaging();
            writer.println("COPY target/" + finalName + " " + finalName);
            writer.println();
            writer.println("EXPOSE 8080");
            writer.println();
            String cmd = "\"java\", \"-jar\", \"" + finalName + "\"";
            if (contextPath != null && !contextPath.isEmpty()) {
                cmd += ", \"--server.servlet.context-path=/" + contextPath + "\"";
            }
            writer.print("CMD [" + cmd + "]");
        }
        getLog().info("Dockerfile created successfully at path: " + dockerfile.getAbsolutePath());
    }

    private void buildDockerImage() throws Exception {
        getLog().info("Building Docker image: " + imageName);
        ProcessBuilder builder = new ProcessBuilder(
                "docker", "build", "-t", imageName, "."
        );
        executeCommand(builder);
        getLog().info("Docker image built successfully");
    }

    private void runDockerContainer() throws Exception {
        removeExistingContainer();

        getLog().info("Running Docker container: " + containerName);
        ProcessBuilder builder = new ProcessBuilder(
                "docker", "run", "-d", "--name", containerName, "-p", portMapping, imageName
        );
        executeCommand(builder);
        getLog().info("Docker container run successfully");
    }

    private void removeExistingContainer() {
        getLog().info("Checking for existing container: " + containerName);
        ProcessBuilder stopBuilder = new ProcessBuilder(
                "docker", "stop", containerName
        );
        ProcessBuilder removeBuilder = new ProcessBuilder(
                "docker", "rm", containerName
        );

        try {
            executeCommand(stopBuilder);
            getLog().info("Stopped container: " + containerName);
        } catch (Exception e) {
            getLog().debug("Container stop failed (might not exist or already stopped): " + containerName);
        }

        try {
            executeCommand(removeBuilder);
            getLog().info("Removed container: " + containerName);
        } catch (Exception e) {
            getLog().debug("Container removal failed (might not exist): " + containerName);
        }
    }

    private void executeCommand(ProcessBuilder processBuilder) throws Exception {
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }
    }
}