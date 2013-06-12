package unit.com.flowdock.jenkins;

import com.flowdock.jenkins.FlowdockAPI;
import com.flowdock.jenkins.PrivateMessage;
import com.flowdock.jenkins.TeamInboxMessage;
import org.junit.Test;

import java.io.IOException;

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
    public void testGetBasicAuthTokenWhenSuppliedWithUserNameAndPassword() throws IOException {
        PrivateMessage privateMessage = new PrivateMessage("test123@test.com", "test123");
        FlowdockAPI api = new FlowdockAPI(privateMessage);
        String expectedAuthToken = "dGVzdDEyM0B0ZXN0LmNvbTp0ZXN0MTIz";

        assertThat(api.getBasicAuthToken(), is(expectedAuthToken));
    }

    @Test
    public void testGetBasicAuthTokenWhenSuppliedWithToken() {
        String authToken = "123";
        PrivateMessage privateMessage = new PrivateMessage(authToken);
        FlowdockAPI api = new FlowdockAPI(privateMessage);
        
        assertThat(api.getBasicAuthToken(), is(authToken));
    }
}