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
    private String packaging;
    private String filePath;
    private String fileName;
    private String workingDirectory;
    private int exposedPort;
    private boolean removeExistingContainer;
}