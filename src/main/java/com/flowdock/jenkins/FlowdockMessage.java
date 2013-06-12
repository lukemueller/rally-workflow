package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static hudson.scm.ChangeLogSet.*;

public abstract class FlowdockMessage {
    protected static final String BASE_API_URL = "https://api.flowdock.com";

    protected String content;
    protected String tags;
    protected String token;
    protected String apiUrl;
    protected AbstractBuild build;
    protected BuildResult buildResult;

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    protected String urlEncode(String data) throws UnsupportedEncodingException {
        if (data == null) {
            return "";
        }

        return URLEncoder.encode(data, "UTF-8");
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = trimToken(token);
    }

    public String trimToken(String token) {
        return token.replaceAll("\\s", "");
    }

    public void setBuild(AbstractBuild build) {
        this.build = build;
    }

    protected void setBuildResult(BuildResult buildResult) {
        this.buildResult = buildResult;
    }

    protected void setBuildAndResult(AbstractBuild build, BuildResult buildResult) {
        setBuild(build);
        setBuildResult(buildResult);
    }

    public String getBuildNumber() {
        return build.getDisplayName().replaceAll("#", "");
    }

    protected static List<Entry> reverseCommits(AbstractBuild build) {
        return reverseCommits(build.getChangeSet());
    }

    protected static List<Entry> reverseCommits(ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet) {
        if(changeLogSet == null || changeLogSet.isEmptySet()) {
            return null;
        }

        List<Entry> commits = new ArrayList();
        for (final Entry entry : changeLogSet) {
            commits.add(0, entry);
        }
        return commits;
    }

    protected static String commitId(Entry commit) {
        String id = commit.getCommitId();
        if (id == null) {
            return "unknown";
        } else {
            return id;
        }
    }

    protected static String commitId(Entry commit, int length) {
        String id = commitId(commit);
        return id.substring(0, Math.max(length, id.length()));
    }

    public abstract String asPostData() throws UnsupportedEncodingException;
    public abstract void setApiUrl();
    protected abstract void setContentFromBuild(AbstractBuild build, BuildResult buildResult) throws FlowdockException;
}