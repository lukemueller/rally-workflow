package com.flowdock.jenkins;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.scm.ChangeLogSet;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class PrivateMessageTest extends FlowdockTestCase {

    public void testGetApiUrlShouldReturnCorrectPrivateMessageUrl() {
        PrivateMessage privateMessage = createPrivateMessage();
        privateMessage.setRecipient("foo");
        String expectedApiUrl = "https://123@api.flowdock.com/private/foo/messages";

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

    private PrivateMessage createPrivateMessage() {
        return new PrivateMessage("123");
    }

    private String[] getRequiredInputParams() {
        return new String[]{"event=message", "&content="};
    }
}
