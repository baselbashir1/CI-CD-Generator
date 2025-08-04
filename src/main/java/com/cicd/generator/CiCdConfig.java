package com.cicd.generator;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CiCdConfig {
    private String baseImage;
    private String imageName;
    private String containerName;
    private String portMapping;
    private String contextPath;
    private String jarPath;
    private String jarName;
    private String workingDirectory;
    private int exposedPort;
    private boolean removeExistingContainer;
}