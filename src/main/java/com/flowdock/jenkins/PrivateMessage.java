package com.flowdock.jenkins;

import java.io.UnsupportedEncodingException;

public class PrivateMessage extends FlowdockMessage {
    @Override
    public String asPostData() throws UnsupportedEncodingException {
        StringBuffer postData = new StringBuffer();
        postData.append("event=").append("message");
        postData.append("&content=").append(urlEncode(content));

        return postData.toString();
    }
}
