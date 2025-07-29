package com.cicd.generator.github;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class GitHubClient {
    private final String apiToken;
    private final String repoOwner;
    private final String repoName;

    public GitHubClient(String apiToken, String repoOwner, String repoName) {
        this.apiToken = apiToken;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    public void triggerWorkflow(String branch) throws Exception {
        String url = String.format("https://api.github.com/repos/%s/%s/actions/workflows/ci.yml/dispatches",
                repoOwner, repoName);

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        post.setHeader("Authorization", "Bearer " + apiToken);
        post.setHeader("Accept", "application/vnd.github.v3+json");
        post.setHeader("Content-Type", "application/json");

        String jsonPayload = String.format("{\"ref\":\"%s\"}", branch);
        post.setEntity(new StringEntity(jsonPayload));

        HttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != 204) {
            throw new Exception("Failed to trigger workflow. Status: " + statusCode);
        }
    }
}