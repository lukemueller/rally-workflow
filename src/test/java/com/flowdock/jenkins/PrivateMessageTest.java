package com.flowdock.jenkins;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class PrivateMessageTest {

    @Test
    public void asPostDataSetsRequiredPostParams() throws UnsupportedEncodingException {
        PrivateMessage privateMessage = new PrivateMessage();
        String postData = privateMessage.asPostData();

        assertThat(postData, containsString("event=message&content="));
    }

}
