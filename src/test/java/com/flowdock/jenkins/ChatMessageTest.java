package com.flowdock.jenkins;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class ChatMessageTest {

    @Test
    public void getApiUrlShouldReturnCorrectPrivateMessageUrl() {
        ChatMessage chatMessage = createPrivateMessage();
        String expectedApiUrl = "https://api.flowdock.com/messages/chat/123";

        assertThat(chatMessage.getApiUrl(), is(expectedApiUrl));
    }

    @Test
    public void asPostDataSetsRequiredPostParams() throws UnsupportedEncodingException {
        ChatMessage chatMessage = createPrivateMessage();
        String postData = chatMessage.asPostData();

        for(String parameter : getRequiredInputParams()) {
            assertThat(postData, containsString(parameter));
        }

    }

    private ChatMessage createPrivateMessage() {
        return new ChatMessage("123");
    }

    private String[] getRequiredInputParams() {
        return new String[] { "external_user_name=", "content=", "tags=" };
    }
}
