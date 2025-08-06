package com.cicd.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@SpringBootApplication
@RequiredArgsConstructor
public class CiCdGeneratorApplication implements CommandLineRunner {

    private final CiCdGenerator generator;

    @Value("${app.file-path}")
    private String filePah;

    @Value("${app.file-name}")
    private String fileName;

    @Value("${app.packaging}")
    private String packaging;

    @Value("${app.context-path}")
    private String contextPath;

    @Override
    public void run(String... args) {
        if (!isRunningFromJar()) {
            File jarFile = new File(filePah + fileName);
            if (!jarFile.exists()) {
                try {
                    buildProjectWithMaven(packaging);
                } catch (Exception e) {
                    log.error("Failed to build project", e);
                    return;
                }
            }
        }

        CiCdExecutionResult result = generator.execute();

        if (result.success()) {
            System.out.println("Spring Boot app containerized successfully!");
        } else {
            System.err.println("Failed to containerize Spring Boot app: " + result.message());
        }
    }

    private boolean isRunningFromJar() {
        try {
            URL resource = getClass().getResource(getClass().getSimpleName() + ".class");
            return resource != null && resource.toString().startsWith("jar:");
        } catch (Exception e) {
            return false;
        }
    }

//    private void buildProjectWithMaven() throws Exception {
//        log.info("Building project with Maven...");
//        String command = isWindows() ? "mvnw.cmd" : "./mvnw";
//
//        ProcessBuilder builder = new ProcessBuilder(command, "clean", "package");
//
//        Map<String, String> env = builder.environment();
//        env.put("JAVA_HOME", System.getProperty("java.home"));
//
//        builder.inheritIO();
//        Process process = builder.start();
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            throw new RuntimeException("Maven build failed with exit code: " + exitCode);
//        }
//        log.info("Project built successfully");
//    }

    private void buildProjectWithMaven(String packagingType) throws Exception {
        log.info("Building project with Maven...");
        String command = isWindows() ? "mvnw.cmd" : "./mvnw";

        List<String> mavenCommands = new ArrayList<>();
        mavenCommands.add(command);
        mavenCommands.add("clean");
        mavenCommands.add("package");

        // Add finalName override (remove .jar extension)
        //String finalName = fileName.replace(".jar", "");
        String finalName = contextPath;
        mavenCommands.add("-DfinalName=" + finalName); // Pass to Maven

        if (packagingType != null && !packagingType.trim().isEmpty()) {
            mavenCommands.add("-Dpackaging.type=" + packagingType.trim());
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
        log.info("Project built successfully with finalName: {}", finalName);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static void main(String[] args) {
        SpringApplication.run(CiCdGeneratorApplication.class, args);
    }
}