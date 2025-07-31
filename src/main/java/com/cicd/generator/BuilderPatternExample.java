package com.cicd.generator;

import com.cicd.generator.lib.CiCdConfig;
import com.cicd.generator.lib.CiCdGenerator;
import com.cicd.generator.lib.CiCdExecutionResult;

public class BuilderPatternExample {

    public static void main(String[] args) {
        // Using builder pattern
        CiCdConfig config = CiCdConfig.builder()
                .imageName("basel-app")
                .containerName("basel-container")
                .baseImage("openjdk:17-jdk-slim")
                .jarPath("target")
                .jarName("BASEL.jar")
                .contextPath("BASEL")
                .portMapping("8080:8080")
                .exposedPort(8080)
                .removeExistingContainer(true)
                .build();

        CiCdGenerator generator = new CiCdGenerator();
        CiCdExecutionResult result = generator.execute(config);

        System.out.println("Execution result: " + result.isSuccess());
    }
}