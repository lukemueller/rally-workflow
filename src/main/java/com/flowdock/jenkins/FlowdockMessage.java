package com.flowdock.jenkins;

import hudson.model.AbstractBuild;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

    protected void setBuildAndResult(AbstractBuild build, BuildResult buildResult) {
        this.build = build;
        this.buildResult = buildResult;
    }

    public abstract String asPostData() throws UnsupportedEncodingException;
    public abstract void setApiUrl();
    protected abstract void setContentFromBuild(AbstractBuild build, BuildResult buildResult);
}