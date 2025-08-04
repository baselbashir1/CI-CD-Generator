package com.cicd.generator;

public record CiCdExecutionResult(
        boolean success,
        String message,
        Exception exception,
        String dockerfilePath
) {
}