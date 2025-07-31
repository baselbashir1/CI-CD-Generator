package com.cicd.generator.lib;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class CiCdGenerator {
    private final CiCdLogger logger;

    public CiCdGenerator() {
        this.logger = CiCdLogger.console();
    }

    public CiCdGenerator(CiCdLogger logger) {
        this.logger = logger;
    }

    /**
     * Execute the complete CI/CD pipeline: create Dockerfile, build image, and run container
     */
    public CiCdExecutionResult execute(CiCdConfig config) {
        try {
            config.validate();

            String dockerfilePath = createDockerfile(config);
            buildDockerImage(config);
            runDockerContainer(config);

            return CiCdExecutionResult.success("CI/CD pipeline executed successfully", dockerfilePath);
        } catch (Exception e) {
            logger.error("CI/CD pipeline failed", e);
            return CiCdExecutionResult.failure("CI/CD pipeline failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create only the Dockerfile
     */
    public CiCdExecutionResult createDockerfileOnly(CiCdConfig config) {
        try {
            config.validate();
            String dockerfilePath = createDockerfile(config);
            return CiCdExecutionResult.success("Dockerfile created successfully", dockerfilePath);
        } catch (Exception e) {
            logger.error("Failed to create Dockerfile", e);
            return CiCdExecutionResult.failure("Failed to create Dockerfile: " + e.getMessage(), e);
        }
    }

    /**
     * Build Docker image only
     */
    public CiCdExecutionResult buildImageOnly(CiCdConfig config) {
        try {
            config.validate();
            buildDockerImage(config);
            return CiCdExecutionResult.success("Docker image built successfully", null);
        } catch (Exception e) {
            logger.error("Failed to build Docker image", e);
            return CiCdExecutionResult.failure("Failed to build Docker image: " + e.getMessage(), e);
        }
    }

    /**
     * Run Docker container only
     */
    public CiCdExecutionResult runContainerOnly(CiCdConfig config) {
        try {
            config.validate();
            runDockerContainer(config);
            return CiCdExecutionResult.success("Docker container started successfully", null);
        } catch (Exception e) {
            logger.error("Failed to run Docker container", e);
            return CiCdExecutionResult.failure("Failed to run Docker container: " + e.getMessage(), e);
        }
    }

    private String createDockerfile(CiCdConfig config) throws Exception {
        logger.info("Creating Dockerfile");

        File dockerfile = new File("Dockerfile");
        String dockerfilePath = dockerfile.getAbsolutePath();

        try (PrintWriter writer = new PrintWriter(dockerfile)) {
            writer.println("FROM " + config.getBaseImage());
            writer.println();
            writer.println("WORKDIR " + config.getWorkingDirectory());
            writer.println();

            // Copy JAR file
            String sourcePath = Paths.get(config.getJarPath(), config.getJarName()).toString();
            writer.println("COPY " + sourcePath + " " + config.getJarName());
            //writer.println("COPY " + "target/" + config.getJarName() + " " + config.getJarName());
            writer.println();

            writer.println("EXPOSE " + config.getExposedPort());
            writer.println();

            // Build CMD
            StringBuilder cmd = new StringBuilder();
            cmd.append("CMD [\"java\", \"-jar\", \"").append(config.getJarName()).append("\"");

            if (config.getContextPath() != null && !config.getContextPath().isEmpty()) {
                cmd.append(", \"--server.servlet.context-path=/").append(config.getContextPath()).append("\"");
            }

            cmd.append("]");
            writer.println(cmd.toString());
        }

        logger.info("Dockerfile created successfully at: " + dockerfilePath);
        return dockerfilePath;
    }

    private void buildDockerImage(CiCdConfig config) throws Exception {
        logger.info("Building Docker image: " + config.getImageName());

        ProcessBuilder builder = new ProcessBuilder(
                "docker", "build", "-t", config.getImageName(), "."
        );

        executeCommand(builder);
        logger.info("Docker image built successfully");
    }

    private void runDockerContainer(CiCdConfig config) throws Exception {
        if (config.isRemoveExistingContainer()) {
            removeExistingContainer(config);
        }

        logger.info("Running Docker container: " + config.getContainerName());

        ProcessBuilder builder = new ProcessBuilder(
                "docker", "run", "-d", "--name", config.getContainerName(),
                "-p", config.getPortMapping(), config.getImageName()
        );

        executeCommand(builder);
        logger.info("Docker container started successfully");
    }

    private void removeExistingContainer(CiCdConfig config) {
        logger.info("Checking for existing container: " + config.getContainerName());

        // Stop container
        ProcessBuilder stopBuilder = new ProcessBuilder(
                "docker", "stop", config.getContainerName()
        );

        try {
            executeCommand(stopBuilder);
            logger.info("Stopped container: " + config.getContainerName());
        } catch (Exception e) {
            logger.debug("Container stop failed (might not exist or already stopped): " + config.getContainerName());
        }

        // Remove container
        ProcessBuilder removeBuilder = new ProcessBuilder(
                "docker", "rm", config.getContainerName()
        );

        try {
            executeCommand(removeBuilder);
            logger.info("Removed container: " + config.getContainerName());
        } catch (Exception e) {
            logger.debug("Container removal failed (might not exist): " + config.getContainerName());
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