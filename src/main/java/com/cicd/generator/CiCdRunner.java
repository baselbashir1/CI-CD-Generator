package com.cicd.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CiCdRunner implements CommandLineRunner {

    @Value("${app.base-image}")
    private String baseImage;

    @Value("${app.image-name}")
    private String imageName;

    @Value("${app.container-name}")
    private String containerName;

    @Value("${app.port-mapping}")
    private String portMapping;

    @Value("${app.context-path}")
    private String contextPath;

    @Value("${app.jar-path}")
    private String jarPath;

    @Value("${app.jar-name}")
    private String jarName;

    @Value("${app.working-directory}")
    private String workingDirectory;

    @Value("${app.exposed-port}")
    private int exposedPort;

    @Value("${app.remove-existing-container}")
    private boolean removeExistingContainer;

    @Override
    public void run(String... args) throws Exception {
        CiCdConfig config = CiCdConfig.builder()
                .baseImage(baseImage)
                .imageName(imageName)
                .containerName(containerName)
                .portMapping(portMapping)
                .contextPath(contextPath)
                .jarPath(jarPath)
                .jarName(jarName)
                .workingDirectory(workingDirectory)
                .exposedPort(exposedPort)
                .removeExistingContainer(removeExistingContainer)
                .build();

        CiCdGenerator generator = new CiCdGenerator();
        CiCdExecutionResult result = generator.execute(config);

        if (result.success()) {
            System.out.println("Spring Boot app containerized successfully!");
        } else {
            System.err.println("Failed to containerize Spring Boot app: " + result.message());
        }
    }
}