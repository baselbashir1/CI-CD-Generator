package com.cicd.generator.gitlab;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class GitLabClient {
    private final String apiToken;
    private final String projectId;
    private final String baseUrl;

    public GitLabClient(String baseUrl, String apiToken, String projectId) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
        this.projectId = projectId;
    }

    public void triggerPipeline(String branch) throws Exception {
        String url = String.format("%s/api/v4/projects/%s/pipeline", baseUrl, projectId);

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        post.setHeader("PRIVATE-TOKEN", apiToken);
        post.setHeader("Content-Type", "application/json");

        String jsonPayload = String.format("{\"ref\":\"%s\"}", branch);
        post.setEntity(new StringEntity(jsonPayload));

        HttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 201) {
            throw new Exception("Failed to trigger pipeline. Status: " + statusCode);
        }
    }
}