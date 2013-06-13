package unit.com.flowdock.jenkins;

import com.flowdock.jenkins.FlowdockAPI;
import com.flowdock.jenkins.PrivateMessage;
import com.flowdock.jenkins.TeamInboxMessage;
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

    @Test
    public void testGetBasicAuthToken() {
        PrivateMessage privateMessage = new PrivateMessage("123");
        FlowdockAPI api = new FlowdockAPI(privateMessage);
        String expectedAuthToken = "MTIz";

        assertThat(api.getBasicAuthToken(), is(expectedAuthToken));
    }
}