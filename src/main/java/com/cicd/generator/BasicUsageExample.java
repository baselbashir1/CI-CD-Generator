package com.cicd.generator;

import com.cicd.generator.lib.CiCdConfig;
import com.cicd.generator.lib.CiCdGenerator;
import com.cicd.generator.lib.CiCdExecutionResult;

public class BasicUsageExample {

    public static void main(String[] args) {
        // Create configuration
        CiCdConfig config = new CiCdConfig()
                .setImageName("my-app-image")
                .setContainerName("my-app-container")
                .setJarPath("target")
                .setJarName("my-app.jar")
                .setContextPath("api")
                .setPortMapping("8080:8080");

        // Create generator and execute
        CiCdGenerator generator = new CiCdGenerator();
        CiCdExecutionResult result = generator.execute(config);

        if (result.isSuccess()) {
            System.out.println("Success: " + result.getMessage());
            System.out.println("Dockerfile created at: " + result.getDockerfilePath());
        } else {
            System.err.println("Failed: " + result.getMessage());
            result.getException().printStackTrace();
        }
    }
}