package com.cicd.generator;

import com.cicd.generator.lib.CiCdConfig;
import com.cicd.generator.lib.CiCdGenerator;
import com.cicd.generator.lib.CiCdExecutionResult;

public class StepByStepExample {

    public static void main(String[] args) {
        CiCdConfig config = CiCdConfig.builder()
                .imageName("step-by-step-app")
                .containerName("step-by-step-container")
                .jarPath("target")
                .jarName("app.jar")
                .build();

        CiCdGenerator generator = new CiCdGenerator();

        // Step 1: Create Dockerfile only
        CiCdExecutionResult dockerfileResult = generator.createDockerfileOnly(config);
        if (!dockerfileResult.isSuccess()) {
            System.err.println("Failed to create Dockerfile: " + dockerfileResult.getMessage());
            return;
        }

        // Step 2: Build image only
        CiCdExecutionResult buildResult = generator.buildImageOnly(config);
        if (!buildResult.isSuccess()) {
            System.err.println("Failed to build image: " + buildResult.getMessage());
            return;
        }

        // Step 3: Run container only
        CiCdExecutionResult runResult = generator.runContainerOnly(config);
        if (!runResult.isSuccess()) {
            System.err.println("Failed to run container: " + runResult.getMessage());
            return;
        }

        System.out.println("All steps completed successfully!");
    }
}