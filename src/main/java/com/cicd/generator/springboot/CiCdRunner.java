package com.cicd.generator.springboot;

import com.cicd.generator.lib.CiCdConfig;
import com.cicd.generator.lib.CiCdGenerator;
import com.cicd.generator.lib.CiCdExecutionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CiCdRunner implements CommandLineRunner {

    @Value("${app.docker.image:my-spring-app}")
    private String imageName;

    @Value("${app.docker.container:my-spring-container}")
    private String containerName;

    @Value("${app.jar.path:target}")
    private String jarPath;

    @Value("${app.jar.name:app.jar}")
    private String jarName;

    @Override
    public void run(String... args) throws Exception {
        CiCdConfig config = CiCdConfig.builder()
                .imageName(imageName)
                .containerName(containerName)
                .jarPath(jarPath)
                .jarName(jarName)
                .baseImage("openjdk:17-jdk-slim")
                .portMapping("8080:8080")
                .build();

        CiCdGenerator generator = new CiCdGenerator();
        CiCdExecutionResult result = generator.execute(config);

        if (result.isSuccess()) {
            System.out.println("Spring Boot app containerized successfully!");
        } else {
            System.err.println("Failed to containerize Spring Boot app: " + result.getMessage());
        }
    }
}