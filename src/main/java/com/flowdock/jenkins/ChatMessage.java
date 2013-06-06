package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

public class ChatMessage extends FlowdockMessage {
    protected String externalUserName;

    public ChatMessage() {
        this.externalUserName = "Jenkins";
    }

    public ChatMessage(String token) {
        this.token = token;
        setApiUrl();
    }

    public void setExternalUserName(String externalUserName) {
        this.externalUserName = externalUserName;
    }

    public String asPostData() throws UnsupportedEncodingException {
        StringBuffer postData = new StringBuffer();
        postData.append("content=").append(urlEncode(content));
        postData.append("&external_user_name=").append(urlEncode(externalUserName));
        postData.append("&tags=").append(urlEncode(tags));
        return postData.toString();
    }

    @Override
    public void setApiUrl() {
        this.apiUrl = MessageFormat.format("{0}/messages/chat/{1}", this.baseApiUrl, this.token);
    }

    public static ChatMessage fromBuild(AbstractBuild build, BuildResult buildResult) {
        ChatMessage msg = new ChatMessage();
        StringBuffer content = new StringBuffer();
        String buildNo = build.getDisplayName().replaceAll("#", "");
        content.append(build.getProject().getName()).append(" build ").append(buildNo);
        content.append(" ").append(buildResult.getHumanResult());

        String rootUrl = Hudson.getInstance().getRootUrl();
        String buildLink = (rootUrl == null) ? null : rootUrl + build.getUrl();
        if(buildLink != null) content.append(" \n").append(buildLink);

        msg.setContent(content.toString());
        return msg;
    }
}
