package com.flowdock.jenkins;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class PrivateMessageTest {

    @Test
    public void getApiUrlShouldReturnCorrectPrivateMessageUrl() {
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedApiUrl = "https://123@api.flowdock.com/private/foo/messages";

        assertThat(privateMessage.getApiUrl(), is(expectedApiUrl));
    }

    @Test
    public void asPostDataSetsRequiredPostParams() throws UnsupportedEncodingException {
        PrivateMessage privateMessage = createPrivateMessage();
        String postData = privateMessage.asPostData();

        for(String parameter : getRequiredInputParams()) {
            assertThat(postData, containsString(parameter));
        }

    }

    private PrivateMessage createPrivateMessage() {
        return new PrivateMessage("123", "foo");
    }

    private String[] getRequiredInputParams() {
        return new String[] { "event=message", "&content=" };
    }
}
