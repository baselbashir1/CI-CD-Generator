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

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    //@Parameter(property = "docker.image.name", defaultValue = "${project.artifactId}-image")
    //private String imageName;

    //@Parameter(property = "docker.container.name", defaultValue = "${project.artifactId}-container")
    //private String containerName;

    //@Parameter(property = "docker.port.mapping", defaultValue = "8080:8080")
    //private String portMapping;

    //@Parameter(property = "docker.base.image", defaultValue = "openjdk:22-jdk-slim")
    //private String baseImage;

    //@Parameter(property = "docker.context.path", defaultValue = "")
    //private String contextPath;

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
            String baseImage = "openjdk:22-jdk-slim";
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
            String contextPath = project.getBuild().getFinalName();
            if (!contextPath.isEmpty()) {
                cmd += ", \"--server.servlet.context-path=/" + contextPath + "\"";
            }
            writer.print("CMD [" + cmd + "]");
        }
        getLog().info("Dockerfile created successfully at path: " + dockerfile.getAbsolutePath());
    }

    private void buildDockerImage() throws Exception {
        String imageName = "basel-image";
        getLog().info("Building Docker image: " + imageName);
        ProcessBuilder builder = new ProcessBuilder(
                "docker", "build", "-t", imageName, "."
        );
        executeCommand(builder);
        getLog().info("Docker image built successfully");
    }

    private void runDockerContainer() throws Exception {
        String imageName = "basel-image";
        String containerName = "basel-container";
        getLog().info("Running Docker container: " + containerName);
        String portMapping = "8080:8080";
        ProcessBuilder builder = new ProcessBuilder(
                "docker", "run", "-d", "--name", containerName, "-p", portMapping, imageName
        );
        executeCommand(builder);
        getLog().info("Docker container run successfully");
    }

    private void executeCommand(ProcessBuilder processBuilder) throws Exception {
        getLog().info("Start executeCommand");
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }
        getLog().info("End executeCommand");
    }
}