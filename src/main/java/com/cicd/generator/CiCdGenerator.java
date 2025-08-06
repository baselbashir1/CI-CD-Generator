package com.cicd.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;

@Slf4j
@Component
public class CiCdGenerator {

    @Value("${app.base-image}")
    private String baseImage;

    @Value("${app.image-name}")
    private String imageName;

    @Value("${app.container-name}")
    private String containerName;

    @Value("${app.port-mapping}")
    private String portMapping;

    @Value("${app.context-path}")
    private String contextPath;

    @Value("${app.file-path}")
    private String filePath;

    @Value("${app.file-name}")
    private String fileName;

    @Value("${app.working-directory}")
    private String workingDirectory;

    @Value("${app.exposed-port}")
    private int exposedPort;

    @Value("${app.remove-existing-container}")
    private boolean removeExistingContainer;

    public CiCdExecutionResult execute() {
        try {
            CiCdConfig config = CiCdConfig.builder()
                    .baseImage(baseImage)
                    .imageName(imageName)
                    .containerName(containerName)
                    .portMapping(portMapping)
                    .contextPath(contextPath)
                    .filePath(filePath)
                    .fileName(fileName)
                    .workingDirectory(workingDirectory)
                    .exposedPort(exposedPort)
                    .removeExistingContainer(removeExistingContainer)
                    .build();

            String dockerfilePath = createDockerfile(config);
            buildDockerImage(config);
            runDockerContainer(config);

            return new CiCdExecutionResult(true, "CI/CD pipeline executed successfully", null, dockerfilePath);
        } catch (Exception e) {
            log.error("CI/CD pipeline failed", e);
            return new CiCdExecutionResult(false, "CI/CD pipeline failed: " + e.getMessage(), e, null);
        }
    }

    private String createDockerfile(CiCdConfig config) throws Exception {
        log.info("Creating Dockerfile");

        File dockerfile = new File("Dockerfile");
        String dockerfilePath = dockerfile.getAbsolutePath();

        try (PrintWriter writer = new PrintWriter(dockerfile)) {
            writer.println("FROM " + config.getBaseImage());
            writer.println();
            writer.println("WORKDIR " + config.getWorkingDirectory());
            writer.println();
            writer.println("COPY " + config.getFilePath() + config.getFileName() + " " + config.getFileName());
            writer.println();
            writer.println("EXPOSE " + config.getExposedPort());
            writer.println();
            StringBuilder cmd = new StringBuilder();
            cmd.append("CMD [\"java\", \"-jar\", \"").append(config.getFileName()).append("\"");
            if (config.getContextPath() != null && !config.getContextPath().isEmpty()) {
                cmd.append(", \"--server.servlet.context-path=/").append(config.getContextPath()).append("\"");
            }
            cmd.append("]");
            writer.println(cmd);
        }

        log.info("Dockerfile created successfully at: {}", dockerfilePath);
        return dockerfilePath;
    }

    private void buildDockerImage(CiCdConfig config) throws Exception {
        log.info("Building Docker image: {}", config.getImageName());

        ProcessBuilder builder = new ProcessBuilder(
                "docker", "build", "-t", config.getImageName(), "."
        );

        executeCommand(builder);
        log.info("Docker image built successfully");
    }

    private void runDockerContainer(CiCdConfig config) throws Exception {
        if (config.isRemoveExistingContainer()) {
            removeExistingContainer(config);
        }

        log.info("Running Docker container: {}", config.getContainerName());

        ProcessBuilder builder = new ProcessBuilder(
                "docker", "run", "-d", "--name", config.getContainerName(),
                "-p", config.getPortMapping(), config.getImageName()
        );

        executeCommand(builder);
        log.info("Docker container started successfully");
    }

    private void removeExistingContainer(CiCdConfig config) {
        log.info("Checking for existing container: {}", config.getContainerName());

        ProcessBuilder stopBuilder = new ProcessBuilder(
                "docker", "stop", config.getContainerName()
        );

        try {
            executeCommand(stopBuilder);
            log.info("Stopped container: {}", config.getContainerName());
        } catch (Exception e) {
            log.debug("Container stop failed (might not exist or already stopped): {}", config.getContainerName());
        }

        ProcessBuilder removeBuilder = new ProcessBuilder(
                "docker", "rm", config.getContainerName()
        );

        try {
            executeCommand(removeBuilder);
            log.info("Removed container: {}", config.getContainerName());
        } catch (Exception e) {
            log.debug("Container removal failed (might not exist): {}", config.getContainerName());
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