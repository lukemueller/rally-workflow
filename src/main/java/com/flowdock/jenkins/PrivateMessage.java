package com.flowdock.jenkins;

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


//    public static PrivateMessage fromBuild(AbstractBuild build, BuildResult buildResult) {
//        PrivateMessage msg = new PrivateMessage();
//        StringBuffer content = new StringBuffer();
//        String buildNo = build.getDisplayName().replaceAll("#", "");
//        content.append(build.getProject().getName()).append(" build ").append(buildNo);
//        content.append(" ").append(buildResult.getHumanResult());
//
//        String rootUrl = Hudson.getInstance().getRootUrl();
//        String buildLink = (rootUrl == null) ? null : rootUrl + build.getUrl();
//        if(buildLink != null) content.append(" \n").append(buildLink);
//
//        msg.setContent(content.toString());
//        return msg;
//    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public void setApiUrl() {
        this.apiUrl = MessageFormat.format("https://{0}@api.flowdock.com/private/{1}/messages", this.token, this.recipient);
    }
}
