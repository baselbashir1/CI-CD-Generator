package com.cicd.generator.gitlab;

import com.cicd.generator.core.CiCdGenerator;
import com.cicd.generator.core.ProjectConfig;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GitLabCiGenerator implements CiCdGenerator {

    @Override
    public void generateConfig(ProjectConfig config, Path outputDir) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/gitlab-ci.mustache");

        Map<String, Object> context = new HashMap<>();
        context.put("config", config);

        try (FileWriter writer = new FileWriter(outputDir.resolve(".gitlab-ci.yml").toFile())) {
            mustache.execute(writer, context);
        }
    }
}