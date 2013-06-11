package com.flowdock.jenkins;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FlowdockAPITest {

    @Test
    public void testGetMessageApiUrl() {
        TeamInboxMessage teamInboxMessage = new TeamInboxMessage("123");
        FlowdockAPI api = new FlowdockAPI(teamInboxMessage);

        assertThat(api.getMessageApiUrl(), is(teamInboxMessage.getApiUrl()));
    }
}
