package com.cicd.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;

@Slf4j
@Component
public class CiCdGenerator {

    @Value("${app.base-image:openjdk:22-jdk-slim}")
    private String baseImage;

    @Value("${app.image-name:app-image}")
    private String imageName;

    @Value("${app.container-name:app-container}")
    private String containerName;

    @Value("${app.port-mapping:8080:8080}")
    private String portMapping;

    @Value("${app.context-path}")
    private String contextPath;

    @Value("${app.packaging}")
    private String packaging;

    @Value("${app.file-path:target/}")
    private String filePath;

    @Value("${app.working-directory:/app}")
    private String workingDirectory;

    @Value("${app.exposed-port:8080}")
    private int exposedPort;

    @Value("${app.remove-existing-container:true}")
    private boolean removeExistingContainer;

    public CiCdExecutionResult execute() {
        try {
            CiCdConfig config = CiCdConfig.builder()
                    .baseImage(baseImage)
                    .imageName(imageName)
                    .containerName(containerName)
                    .portMapping(portMapping)
                    .contextPath(contextPath)
                    .packaging(packaging)
                    .filePath(filePath)
                    .fileName(contextPath + "." + packaging)
                    .workingDirectory(workingDirectory)
                    .exposedPort(exposedPort)
                    .removeExistingContainer(removeExistingContainer)
                    .build();

            String dockerfilePath = createDockerfile(config);
            createDockerComposeFile(config);
            createGitlabCiFile(config);
            // buildDockerImage(config);
            // runDockerContainer(config);
            runDockerCompose(config);

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
            writer.print(cmd);
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

    private void createDockerComposeFile(CiCdConfig config) throws Exception {
        log.info("Creating docker-compose.yml");
        File composeFile = new File("docker-compose.yml");
        try (PrintWriter writer = new PrintWriter(composeFile)) {
            writer.println("services:");
            writer.println("  " + "app-service" + ":");
            writer.println("    build:");
            writer.println("      context: .");
            writer.println("      dockerfile: Dockerfile");
            writer.println("    container_name: " + config.getContainerName());
            writer.println("    ports:");
            writer.println("      - \"" + config.getPortMapping() + "\"");
            writer.print("    restart: always");
        }
        log.info("docker-compose.yml created at: {}", composeFile.getAbsolutePath());
    }

    private void runDockerCompose(CiCdConfig config) throws Exception {
        log.info("Removing existing containers with docker-compose");
        ProcessBuilder downBuilder = new ProcessBuilder("docker-compose", "down");
        executeCommand(downBuilder);

        log.info("Building and starting containers with docker-compose");
        ProcessBuilder upBuilder = new ProcessBuilder(
                "docker-compose", "up", "-d", "--build", "--force-recreate"
        );
        executeCommand(upBuilder);
    }

    private void createGitlabCiFile(CiCdConfig config) throws Exception {
        log.info("Creating .gitlab-ci.yml");
        File gitlabCiFile = new File(".gitlab-ci.yml");
        try (PrintWriter writer = new PrintWriter(gitlabCiFile)) {
            writer.println("stages:");
            writer.println("  - build");
            writer.println("  - deploy");
            writer.println();
            writer.println("variables:");
            writer.println("  IMAGE_NAME: " + config.getImageName());
            writer.println("  CONTAINER_NAME: " + config.getContainerName());
            writer.println("  PORT_MAPPING: \"" + config.getPortMapping() + "\"");
            writer.println();
            writer.println("build:");
            writer.println("  stage: build");
            writer.println("  script:");
            writer.println("    - docker build -t $IMAGE_NAME .");
            writer.println("  only:");
            writer.println("    - main");
            writer.println();
            writer.println("deploy:");
            writer.println("  stage: deploy");
            writer.println("  script:");
            writer.println("    - docker stop $CONTAINER_NAME || true"); // Graceful cleanup
            writer.println("    - docker rm $CONTAINER_NAME || true");
            writer.println("    - docker run -d --name $CONTAINER_NAME -p $PORT_MAPPING $IMAGE_NAME");
            writer.println("  only:");
            writer.print("    - main");
        }
        log.info(".gitlab-ci.yml created at: {}", gitlabCiFile.getAbsolutePath());
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