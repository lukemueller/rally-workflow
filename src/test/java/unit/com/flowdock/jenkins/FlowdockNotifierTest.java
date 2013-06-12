package unit.com.flowdock.jenkins;


import com.flowdock.jenkins.FlowdockNotifier;
import com.flowdock.jenkins.PrivateMessage;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class FlowdockNotifierTest {

    @Test
    public void testCreatePrivateMessageWhenConfiguredWithUsernameAndPassword() {
        String username = "foo";
        String password = "bar";
        FlowdockNotifier notifier = createFlowdockNotifier(null, username, password);
        PrivateMessage privateMessage = notifier.createPrivateMessage();

        assertThat(privateMessage.getToken(), is(nullValue()));
        assertThat(privateMessage.getUsername(), is(username));
        assertThat(privateMessage.getPassword(), is(password));
    }

    @Test
    public void testCreatePrivateMessageWhenConfiguredWithBasicAuthToken() {
        String basicAuthToken = "123";
        FlowdockNotifier notifier = createFlowdockNotifier(basicAuthToken, null, null);
        PrivateMessage privateMessage = notifier.createPrivateMessage();

        assertThat(privateMessage.getToken(), is(basicAuthToken));
        assertThat(privateMessage.getUsername(), is(nullValue()));
        assertThat(privateMessage.getPassword(), is(nullValue()));
    }

    private FlowdockNotifier createFlowdockNotifier(String basicAuthToken, String username, String password) {
        return new FlowdockNotifier(null, null, null, null, basicAuthToken, username, password, null, null, null, null, null, null);
    }
}
