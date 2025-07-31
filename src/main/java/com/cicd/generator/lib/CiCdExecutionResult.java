package com.cicd.generator.lib;

public class CiCdExecutionResult {
    private final boolean success;
    private final String message;
    private final Exception exception;
    private final String dockerfilePath;

    private CiCdExecutionResult(boolean success, String message, Exception exception, String dockerfilePath) {
        this.success = success;
        this.message = message;
        this.exception = exception;
        this.dockerfilePath = dockerfilePath;
    }

    public static CiCdExecutionResult success(String message, String dockerfilePath) {
        return new CiCdExecutionResult(true, message, null, dockerfilePath);
    }

    public static CiCdExecutionResult failure(String message, Exception exception) {
        return new CiCdExecutionResult(false, message, exception, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Exception getException() { return exception; }
    public String getDockerfilePath() { return dockerfilePath; }
}