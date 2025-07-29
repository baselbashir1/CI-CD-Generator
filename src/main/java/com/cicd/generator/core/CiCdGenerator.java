package com.cicd.generator.core;

import java.io.IOException;
import java.nio.file.Path;

public interface CiCdGenerator {
    void generateConfig(ProjectConfig config, Path outputDir) throws IOException;
}