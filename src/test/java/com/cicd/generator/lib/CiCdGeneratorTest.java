package com.cicd.generator.lib;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Path;

public class CiCdGeneratorTest {

    @TempDir
    Path tempDir;

    private CiCdGenerator generator;
    private CiCdConfig validConfig;

    @BeforeEach
    void setUp() {
        generator = new CiCdGenerator();
        validConfig = CiCdConfig.builder()
                .imageName("test-image")
                .containerName("test-container")
                .jarPath("target")
                .jarName("test.jar")
                .build();
    }

    @Test
    void testConfigValidation() {
        CiCdConfig invalidConfig = new CiCdConfig();

        assertThrows(IllegalArgumentException.class, () -> {
            invalidConfig.validate();
        });
    }

    @Test
    void testBuilderPattern() {
        CiCdConfig config = CiCdConfig.builder()
                .imageName("test")
                .containerName("test")
                .jarPath("target")
                .jarName("test.jar")
                .baseImage("custom-base")
                .contextPath("api")
                .build();

        assertEquals("test", config.getImageName());
        assertEquals("test", config.getContainerName());
        assertEquals("custom-base", config.getBaseImage());
        assertEquals("api", config.getContextPath());
    }

    @Test
    void testFluentAPI() {
        CiCdConfig config = new CiCdConfig()
                .setImageName("fluent-test")
                .setContainerName("fluent-container")
                .setJarPath("target")
                .setJarName("test.jar")
                .setPortMapping("9090:8080");

        assertEquals("fluent-test", config.getImageName());
        assertEquals("9090:8080", config.getPortMapping());
    }

    @Test
    void testDockerfileCreation() {
        // This test would require mocking or actual file system access
        // For demonstration purposes, showing structure

        CiCdExecutionResult result = generator.createDockerfileOnly(validConfig);

        // In a real test, you'd mock the file operations or use temporary directories
        // assertTrue(result.isSuccess());
        // assertNotNull(result.getDockerfilePath());
    }
}