package com.flowdock.jenkins;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class FlowdockMessage {
    protected final String baseApiUrl = "https://api.flowdock.com";
    protected String content;
    protected String tags;
    protected String token;
    protected String apiUrl;

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

    public abstract String asPostData() throws UnsupportedEncodingException;

    public abstract void setApiUrl();
}