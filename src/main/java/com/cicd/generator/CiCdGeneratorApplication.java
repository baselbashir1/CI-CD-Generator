package com.cicd.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@SpringBootApplication
@RequiredArgsConstructor
public class CiCdGeneratorApplication implements CommandLineRunner {

    private final CiCdGenerator generator;

    @Value("${app.packaging}")
    private String packaging;

    @Value("${app.context-path}")
    private String contextPath;

    @Override
    public void run(String... args) {
        try {
            buildProjectWithMaven();

            File packagedFile = new File("target/" + contextPath + "." + packaging);
            if (!packagedFile.exists()) {
                log.error("Packaged file not found at: {}", packagedFile.getAbsolutePath());
                System.err.println("Failed to find packaged file: " + packagedFile.getAbsolutePath());
                return;
            }

            log.info("Packaged file found: {}", packagedFile.getAbsolutePath());

            CiCdExecutionResult result = generator.execute();

            if (result.success()) {
                System.out.println("Spring Boot app containerized successfully!");
                System.out.println("Dockerfile created at: " + result.dockerfilePath());
                System.out.println("docker-compose.yml created at: " + result.dockerfilePath());
                System.out.println("gitlab-ci.yml created at: " + result.dockerfilePath());
                System.out.println("Packaged app available at: " + packagedFile.getAbsolutePath());
            } else {
                System.err.println("Failed to containerize Spring Boot app: " + result.message());
            }
        } catch (Exception e) {
            log.error("Application execution failed", e);
            System.err.println("Application execution failed: " + e.getMessage());
        } finally {
            System.exit(0);
        }
    }

    private void buildProjectWithMaven() throws Exception {
        log.info("Building project with Maven...");
        String command = isWindows() ? "mvnw.cmd" : "./mvnw";

        List<String> mavenCommands = new ArrayList<>();
        mavenCommands.add(command);
        mavenCommands.add("clean");
        mavenCommands.add("package");
        mavenCommands.add("-DskipTests");

        String finalName = contextPath;
        mavenCommands.add("-Dbuild.name=" + finalName);

        if (packaging != null && !packaging.trim().isEmpty()) {
            mavenCommands.add("-Dpackaging.type=" + packaging.trim());
        }

        ProcessBuilder builder = new ProcessBuilder(mavenCommands);
        Map<String, String> env = builder.environment();
        env.put("JAVA_HOME", System.getProperty("java.home"));
        builder.inheritIO();

        Process process = builder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Maven build failed with exit code: " + exitCode);
        }
        log.info("Project built successfully as {} with finalName: {}", packaging, finalName);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static void main(String[] args) {
        SpringApplication.run(CiCdGeneratorApplication.class, args);
    }
}