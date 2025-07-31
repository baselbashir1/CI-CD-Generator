package com.cicd.generator.lib;

public class CiCdConfig {
    private String baseImage = "openjdk:17-jdk-slim";
    private String imageName;
    private String containerName;
    private String portMapping = "8080:8080";
    private String contextPath;
    private String jarPath;
    private String jarName;
    private String workingDirectory = "/app";
    private int exposedPort = 8080;
    private boolean removeExistingContainer = true;

    // Constructors
    public CiCdConfig() {
    }

    public CiCdConfig(String imageName, String containerName) {
        this.imageName = imageName;
        this.containerName = containerName;
    }

    // Builder pattern
    public static CiCdConfigBuilder builder() {
        return new CiCdConfigBuilder();
    }

    // Getters and Setters
    public String getBaseImage() {
        return baseImage;
    }

    public CiCdConfig setBaseImage(String baseImage) {
        this.baseImage = baseImage;
        return this;
    }

    public String getImageName() {
        return imageName;
    }

    public CiCdConfig setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String getContainerName() {
        return containerName;
    }

    public CiCdConfig setContainerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    public String getPortMapping() {
        return portMapping;
    }

    public CiCdConfig setPortMapping(String portMapping) {
        this.portMapping = portMapping;
        return this;
    }

    public String getContextPath() {
        return contextPath;
    }

    public CiCdConfig setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public String getJarPath() {
        return jarPath;
    }

    public CiCdConfig setJarPath(String jarPath) {
        this.jarPath = jarPath;
        return this;
    }

    public String getJarName() {
        return jarName;
    }

    public CiCdConfig setJarName(String jarName) {
        this.jarName = jarName;
        return this;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public CiCdConfig setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public int getExposedPort() {
        return exposedPort;
    }

    public CiCdConfig setExposedPort(int exposedPort) {
        this.exposedPort = exposedPort;
        return this;
    }

    public boolean isRemoveExistingContainer() {
        return removeExistingContainer;
    }

    public CiCdConfig setRemoveExistingContainer(boolean removeExistingContainer) {
        this.removeExistingContainer = removeExistingContainer;
        return this;
    }

    // Validation
    public void validate() {
        if (imageName == null || imageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Image name is required");
        }
        if (containerName == null || containerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Container name is required");
        }
        if (jarPath == null || jarPath.trim().isEmpty()) {
            throw new IllegalArgumentException("JAR path is required");
        }
        if (jarName == null || jarName.trim().isEmpty()) {
            throw new IllegalArgumentException("JAR name is required");
        }
    }
}