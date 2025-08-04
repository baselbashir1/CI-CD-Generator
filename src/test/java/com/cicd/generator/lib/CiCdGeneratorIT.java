package com.cicd.generator.lib;

import com.cicd.generator.CiCdConfig;
import com.cicd.generator.CiCdExecutionResult;
import com.cicd.generator.CiCdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CiCdGeneratorIT {

    @Test
    void testDockerfileCreation() {
        log.info("testDockerfileCreation");

        CiCdConfig config = CiCdConfig.builder()
                .baseImage("openjdk:22-jdk-slim")
                .imageName("basel-image")
                .containerName("basel-container")
                .portMapping("8080:8080")
                .contextPath("BASEL")
                .jarPath("target/")
                .jarName("BASEL.jar")
                .workingDirectory("/app")
                .exposedPort(8080)
                .removeExistingContainer(true)
                .build();

        CiCdGenerator generator = new CiCdGenerator();
        CiCdExecutionResult result = generator.execute(config);

        if (result.success()) {
            log.info("testDockerfileCreation success");
        } else {
            log.info("testDockerfileCreation failed");
        }
    }
}