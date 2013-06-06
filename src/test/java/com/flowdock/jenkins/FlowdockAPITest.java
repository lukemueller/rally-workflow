package com.flowdock.jenkins;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FlowdockAPITest {

    @Test
    public void itShouldUseTheMessageApiUrl() {
        PrivateMessage privateMessage = new PrivateMessage("123", "foo");
        FlowdockAPI api = new FlowdockAPI(privateMessage);

        assertThat(api.getApiUrl(), is(privateMessage.getApiUrl()));
    }


}
