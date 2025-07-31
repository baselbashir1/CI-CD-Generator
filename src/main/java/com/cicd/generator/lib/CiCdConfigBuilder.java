package com.cicd.generator.lib;

public class CiCdConfigBuilder {
    private final CiCdConfig config = new CiCdConfig();

    public CiCdConfigBuilder baseImage(String baseImage) {
        config.setBaseImage(baseImage);
        return this;
    }

    public CiCdConfigBuilder imageName(String imageName) {
        config.setImageName(imageName);
        return this;
    }

    public CiCdConfigBuilder containerName(String containerName) {
        config.setContainerName(containerName);
        return this;
    }

    public CiCdConfigBuilder portMapping(String portMapping) {
        config.setPortMapping(portMapping);
        return this;
    }

    public CiCdConfigBuilder contextPath(String contextPath) {
        config.setContextPath(contextPath);
        return this;
    }

    public CiCdConfigBuilder jarPath(String jarPath) {
        config.setJarPath(jarPath);
        return this;
    }

    public CiCdConfigBuilder jarName(String jarName) {
        config.setJarName(jarName);
        return this;
    }

    public CiCdConfigBuilder workingDirectory(String workingDirectory) {
        config.setWorkingDirectory(workingDirectory);
        return this;
    }

    public CiCdConfigBuilder exposedPort(int exposedPort) {
        config.setExposedPort(exposedPort);
        return this;
    }

    public CiCdConfigBuilder removeExistingContainer(boolean removeExistingContainer) {
        config.setRemoveExistingContainer(removeExistingContainer);
        return this;
    }

    public CiCdConfig build() {
        config.validate();
        return config;
    }
}