package com.cicd.generator;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class PluginRunnerTest {

    @Test
    public void executeMojoDirectly() throws Exception {
        CiCdMojo mojo = new CiCdMojo();

        // Configure Mojo parameters via reflection
        Field projectField = CiCdMojo.class.getDeclaredField("project");
        projectField.setAccessible(true);

        // Create a minimal Maven project
        MavenProject project = new MavenProject();
        project.setPackaging("jar");
        project.setArtifactId("basel.generator");
        project.getBuild().setFinalName("BASEL");

        // Set parameters
        projectField.set(mojo, project);

        // Execute the Mojo
        mojo.execute();
    }
}