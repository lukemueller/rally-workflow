package com.flowdock.jenkins;

import hudson.model.AbstractBuild;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

public class PrivateMessage extends FlowdockMessage {

    private String apiUrl;
    private final String recipient;

    public PrivateMessage(String token, String recipient) {
        this.token = token;
        this.recipient = recipient;
        setApiUrl();
    }

    @Override
    public String asPostData() throws UnsupportedEncodingException {
        StringBuffer postData = new StringBuffer();
        postData.append("event=").append("message");
        postData.append("&content=").append(urlEncode(content));

        return postData.toString();
    }

    @Override
    public String getApiUrl() {
        return this.apiUrl;
    }

    @Override
    public void setApiUrl() {
        this.apiUrl = MessageFormat.format("https://{0}@api.flowdock.com/private/{1}/messages", this.token, this.recipient);
    }

    @Override
    protected void setContentFromBuild(AbstractBuild build, BuildResult buildResult) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
