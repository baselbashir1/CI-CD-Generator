package com.cicd.generator;

import com.cicd.generator.lib.CiCdConfig;
import com.cicd.generator.lib.CiCdGenerator;
import com.cicd.generator.lib.CiCdLogger;
import com.cicd.generator.lib.CiCdExecutionResult;

public class CustomLoggerExample {

    // Custom logger implementation
    static class CustomLogger implements CiCdLogger {
        @Override
        public void info(String message) {
            System.out.println("[CUSTOM-INFO] " + message);
        }

        @Override
        public void debug(String message) {
            System.out.println("[CUSTOM-DEBUG] " + message);
        }

        @Override
        public void error(String message, Exception exception) {
            System.err.println("[CUSTOM-ERROR] " + message);
            if (exception != null) {
                exception.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        CiCdConfig config = CiCdConfig.builder()
                .imageName("custom-logger-app")
                .containerName("custom-logger-container")
                .jarPath("target")
                .jarName("app.jar")
                .build();

        // Use custom logger
        CiCdGenerator generator = new CiCdGenerator(new CustomLogger());
        CiCdExecutionResult result = generator.execute(config);

        System.out.println("Result: " + result.isSuccess());
    }
}