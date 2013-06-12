package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.scm.ChangeLogSet;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class PrivateMessageTest extends FlowdockTestCase {

    public void testGetApiUrl() {
        PrivateMessage privateMessage = createPrivateMessage();
        privateMessage.setRecipientId("foo");
        String expectedApiUrl = "https://api.flowdock.com/private/foo/messages";

        assertThat(privateMessage.getApiUrl(), is(expectedApiUrl));
    }

    public void testAsPostDataSetsRequiredPostParams() throws UnsupportedEncodingException {
        PrivateMessage privateMessage = createPrivateMessage();
        String postData = privateMessage.asPostData();

        for (String parameter : getRequiredInputParams()) {
            assertThat(postData, containsString(parameter));
        }
    }

    public void testGetRallyAuthorWithPairingAliasFromSingleScmChange() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "pairing+foo+bar");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "foo@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    public void testGetRallyAuthorWithPairingAliasFromMultipleScmChanges() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "pairing+foo+bar", "pairing+bar+baz");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "bar@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    public void testGetRallyAuthorFromSingleScmChange() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "lmueller");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "lmueller@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    public void testGetRallyAuthorFromMultipleScmChanges() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "foo", "lmueller");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "lmueller@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    public void testGetUserId() throws FlowdockException {
        String username = "test123@test.com";
        PrivateMessage privateMessage = new PrivateMessage(username, "test123");
        privateMessage.setRecipientEmail(username);

        String userId = privateMessage.getUserId();
        String expectedUserId = "39863";

        assertThat(userId, is(expectedUserId));
    }

    private PrivateMessage createPrivateMessage() {
        return new PrivateMessage("username", "password");
    }

    private String[] getRequiredInputParams() {
        return new String[]{"event=message", "content="};
    }
}
