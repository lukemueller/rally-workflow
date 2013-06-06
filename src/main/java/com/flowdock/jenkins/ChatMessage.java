package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

public class ChatMessage extends FlowdockMessage {
    protected String externalUserName;


    public ChatMessage(String token) {
        this.token = token;
        this.externalUserName = "Jenkins";
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
        this.apiUrl = MessageFormat.format("{0}/messages/chat/{1}", this.BASE_API_URL, this.token);
    }

    @Override
    protected void setContentFromBuild(AbstractBuild build, BuildResult buildResult) {
        setBuildAndResult(build, buildResult);
        StringBuffer content = new StringBuffer();
        content.append(build.getProject().getName()).append(" build ").append(getBuildNumber());
        content.append(" ").append(buildResult.getHumanResult());

        String rootUrl = Hudson.getInstance().getRootUrl();
        String buildLink = (rootUrl == null) ? null : rootUrl + build.getUrl();
        if(buildLink != null) content.append(" \n").append(buildLink);

        setContent(content.toString());
    }
}
