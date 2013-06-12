package unit.com.flowdock.jenkins;

import com.flowdock.jenkins.TeamInboxMessage;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class TeamInboxMessageTest {

    @Test
    public void getApiUrlShouldReturnCorrectTeamInboxMessageUrl() {
        TeamInboxMessage teamInboxMessage = createTeamInboxMessage();
        String expectedApiUrl = "https://api.flowdock.com/team_inbox/messages/123";

        assertThat(teamInboxMessage.getApiUrl(), is(expectedApiUrl));
    }

    @Test
    public void asPostDataSetsRequiredPostParams() throws UnsupportedEncodingException {
        TeamInboxMessage teamInboxMessage = createTeamInboxMessage();
        String postData = teamInboxMessage.asPostData();

        for (String parameter : getRequiredInputParams()) {
            assertThat(postData, containsString(parameter));
        }
    }

    private TeamInboxMessage createTeamInboxMessage() {
        return new TeamInboxMessage("123");
    }

    private String[] getRequiredInputParams() {
        return new String[] { "subject=", "content=", "from_address=", "from_name=", "source=", "project=", "link=", "tags=" };
    }


}
