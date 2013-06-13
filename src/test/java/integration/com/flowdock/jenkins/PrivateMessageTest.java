package integration.com.flowdock.jenkins;

import com.flowdock.jenkins.FlowdockNotifier;
import com.flowdock.jenkins.PrivateMessage;
import com.flowdock.jenkins.exception.FlowdockException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.scm.ChangeLogSet;
import org.jvnet.hudson.test.UnstableBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PrivateMessageTest extends FlowdockTestCase {

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

    public void testGetRallyAuthorWithShortHandNameFromSingleScmChange() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "lmueller");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "lmueller@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    public void testGetRallyAuthorWithShortHandNameFromMultipleScmChanges() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "foo", "lmueller");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "lmueller@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    public void testGetRallyAuthorWithFullNameFromSingleScmChange() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "Luke Mueller");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "lmueller@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    public void testGetRallyAuthorWithFullNameFromMultipleScmChanges() throws Exception {
        FreeStyleProject project = createProject();
        FreeStyleBuild build = kickOffBuild(project);

        ChangeLogSet<? extends ChangeLogSet.Entry> changes = getChangeSetWithAuthors(build, "foo", "Luke Mueller");
        PrivateMessage privateMessage = createPrivateMessage();
        String expectedAuthor = "lmueller@rallydev.com";

        assertThat(privateMessage.getRallyAuthor(changes), is(expectedAuthor));
    }

    /*
     * Hits Flowdock API to get real user data from your subscription
     * Requires that integration.cfg is correctly populated with real Flowdock information
     */
    public void testGetUserId() throws FlowdockException {
        PrivateMessage privateMessage = new PrivateMessage(getSenderTokenFromConfig());
        privateMessage.setRecipientEmail(getRecipientEmailFromConfig());

        String actualUserId = privateMessage.getUserId();
        String expectedUserId = getRecipientIdFromConfig();

        assertThat(actualUserId, is(expectedUserId));
    }

    /*v
     * Actually send a message to flowdock through your local headless Jenkins instance
     * Requires that integration.cfg is correctly populated with real Flowdock information
     */
    public void testSendingPrivateMessage() throws Exception {
        FreeStyleProject project = createProject(new UnstableBuilder());
        FlowdockNotifier notifier = createFlowdockNotifierForSendingPrivateMessages();
        addFlowdockNotificationToPostBuildActions(project, notifier);
        kickOffBuild(project);

        assertThat(true, is(true));
    }

    private PrivateMessage createPrivateMessage() {
        return new PrivateMessage("123");
    }
}