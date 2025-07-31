package com.cicd.generator;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class PluginRunnerTest {

    @Test
    public void executeMojoDirectly() throws Exception {
        CiCdMojo mojo = new CiCdMojo();

        MavenProject project = new MavenProject();
        project.setPackaging("jar");
        project.setArtifactId("basel.generator");
        project.getBuild().setFinalName("BASEL");

        String contextPath = project.getBuild().getFinalName();

        setField(mojo, "project", project);
        setField(mojo, "baseImage", "openjdk:22-jdk-slim"); // "eclipse-temurin:17-jdk-alpine"
        setField(mojo, "imageName", "basel-image");
        setField(mojo, "containerName", "basel-container");
        setField(mojo, "portMapping", "8080:8080");
        setField(mojo, "contextPath", contextPath);

        mojo.execute();
    }

    private void setField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}