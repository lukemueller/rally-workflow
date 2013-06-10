package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;
import org.junit.Test;
import org.jvnet.hudson.test.FakeChangeLogSCM;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.jvnet.hudson.test.FakeChangeLogSCM.FakeChangeLogSet;
import static org.mockito.Mockito.mock;

public class PrivateMessageTest {

    @Test
    public void getApiUrlShouldReturnCorrectPrivateMessageUrl() {
        PrivateMessage privateMessage = createPrivateMessage();
        privateMessage.setRecipient("foo");
        String expectedApiUrl = "https://123@api.flowdock.com/private/foo/messages";

        assertThat(privateMessage.getApiUrl(), is(expectedApiUrl));
    }

    @Test
    public void asPostDataSetsRequiredPostParams() throws UnsupportedEncodingException {
        PrivateMessage privateMessage = createPrivateMessage();
        String postData = privateMessage.asPostData();

        for (String parameter : getRequiredInputParams()) {
            assertThat(postData, containsString(parameter));
        }
    }

    @Test
    public void getAuthorReturnsRallyEmailForBuildWithScmChanges() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        ChangeLogSet<? extends ChangeLogSet.Entry> changes = addScmChangeWithAuthor(build, "pairing+foo+bar");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "foo@rallydev.com";

        assertThat(privateMessage.getAuthor(changes), is(expectedAuthor));
    }

    private FakeChangeLogSet addScmChangeWithAuthor(AbstractBuild build, String author) {
        ArrayList<FakeChangeLogSCM.EntryImpl> entries = new ArrayList<FakeChangeLogSCM.EntryImpl>();

        FakeChangeLogSCM.EntryImpl entry = new FakeChangeLogSCM.EntryImpl();
        entry.withAuthor(author);
        entries.add(entry);

        return new FakeChangeLogSet(build, entries);
    }

    private PrivateMessage createPrivateMessage() {
        return new PrivateMessage("123");
    }

    private String[] getRequiredInputParams() {
        return new String[]{"event=message", "&content="};
    }
}
