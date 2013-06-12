package unit.com.flowdock.jenkins;

import com.flowdock.jenkins.PrivateMessage;
import com.flowdock.jenkins.exception.FlowdockException;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class PrivateMessageTest {

    @Test
    public void testGetApiUrl() {
        PrivateMessage privateMessage = createPrivateMessage();
        privateMessage.setRecipientId("foo");
        String expectedApiUrl = "https://api.flowdock.com/private/foo/messages";

        assertThat(privateMessage.getApiUrl(), is(expectedApiUrl));
    }

    @Test
    public void testAsPostDataSetsRequiredPostParams() throws UnsupportedEncodingException {
        PrivateMessage privateMessage = createPrivateMessage();
        String postData = privateMessage.asPostData();

        for (String parameter : getRequiredInputParams()) {
            assertThat(postData, containsString(parameter));
        }
    }

    @Test
    public void testGetUserId() throws FlowdockException {
        String username = "test123@test.com";
        PrivateMessage privateMessage = new PrivateMessage(username, "test123");
        privateMessage.setRecipientEmail(username);

        String actualUserId = privateMessage.getUserId();
        String expectedUserId = "39863";

        assertThat(actualUserId, is(expectedUserId));
    }

    private PrivateMessage createPrivateMessage() {
        return new PrivateMessage("username", "password");
    }

    private String[] getRequiredInputParams() {
        return new String[]{"event=message", "content="};
    }
}
