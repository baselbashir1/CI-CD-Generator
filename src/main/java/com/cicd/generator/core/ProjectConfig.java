package com.cicd.generator.core;

import java.util.List;

public class ProjectConfig {
    private String projectName;
    private String buildTool; // maven, gradle, sbt
    private List<String> stages; // build, test, deploy, etc.
    private String jdkVersion;
    private String dockerImage;
    private String mainBranch = "main";
    private String deployEnvironment; // production, staging

    // Getters and setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }

    public List<String> getStages() {
        return stages;
    }

    public void setStages(List<String> stages) {
        this.stages = stages;
    }

    public String getJdkVersion() {
        return jdkVersion;
    }

    public void setJdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public String getMainBranch() {
        return mainBranch;
    }

    public void setMainBranch(String mainBranch) {
        this.mainBranch = mainBranch;
    }

    public String getDeployEnvironment() {
        return deployEnvironment;
    }

    public void setDeployEnvironment(String deployEnvironment) {
        this.deployEnvironment = deployEnvironment;
    }
}