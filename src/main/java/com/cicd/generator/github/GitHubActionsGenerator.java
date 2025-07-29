package com.cicd.generator.github;

import com.cicd.generator.core.CiCdGenerator;
import com.cicd.generator.core.ProjectConfig;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GitHubActionsGenerator implements CiCdGenerator {

    @Override
    public void generateConfig(ProjectConfig config, Path outputDir) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/github-actions.mustache");

        Map<String, Object> context = new HashMap<>();
        context.put("config", config);

        Path workflowDir = outputDir.resolve(".github/workflows");
        Files.createDirectories(workflowDir);

        try (FileWriter writer = new FileWriter(workflowDir.resolve("ci.yml").toFile())) {
            mustache.execute(writer, context);
        }
    }
}