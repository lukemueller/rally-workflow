package integration.com.flowdock.jenkins;

import com.flowdock.jenkins.*;
import com.flowdock.jenkins.exception.FlowdockException;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.MockBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.jvnet.hudson.test.FakeChangeLogSCM.EntryImpl;
import static org.jvnet.hudson.test.FakeChangeLogSCM.FakeChangeLogSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FlowdockTestCase extends HudsonTestCase {

    public FreeStyleProject createProject(MockBuilder builder) throws IOException {
        FreeStyleProject project = createFreeStyleProject();
        project.getBuildersList().add(builder);

        return project;
    }

    public FreeStyleProject createProject() throws IOException {
        return createFreeStyleProject();
    }

    /*
     * Returns a notifier spy with a mock API to prevent actually sending messages to Flowdock
     */
    public FlowdockNotifier createFlowdockNotifierSpy(String chatNotification, String privateNotification) throws UnsupportedEncodingException, FlowdockException {
        FlowdockNotifier notifier = new FlowdockNotifier(
                "123", null, chatNotification, privateNotification, null, "true", "true", "true", "true", "true", "true");
        FlowdockNotifier notifierSpy = spy(notifier);
        doReturn(mock(FlowdockAPI.class)).when(notifierSpy).getFlowdockAPIForMessage(any(FlowdockMessage.class));
        doNothing().when(notifierSpy).buildAndSendMessage(any(AbstractBuild.class), any(BuildResult.class), any(FlowdockMessage.class));

        return notifierSpy;
    }

    /*
     * Returns a notifier that will ONLY send a PrivateMessage configured with sender and recipient from integration.cfg
     */
    public FlowdockNotifier createFlowdockNotifierForSendingPrivateMessages() throws FlowdockException, UnsupportedEncodingException {
        FlowdockNotifier notifier = new FlowdockNotifier(
                "123", null, "false", "true", null, "true", "true", "true", "true", "true", "true");

        FlowdockNotifier notifierSpy = spy(notifier);
        doNothing().when(notifierSpy).sendTeamInboxMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
        doReturn(getPrivateMessageSpy()).when(notifierSpy).createPrivateMessage();

        return notifierSpy;
    }

    private PrivateMessage getPrivateMessageSpy() throws FlowdockException {
        PrivateMessage privateMessage = new PrivateMessage(getSenderTokenFromConfig());
        privateMessage.setRecipientId(getRecipientIdFromConfig());
        PrivateMessage privateMessageSpy = spy(privateMessage);

        doNothing().when(privateMessageSpy).setRecipient();

        return privateMessageSpy;
    }

    protected String getSenderTokenFromConfig() {
        return (String) getConfigAsJson().get("senderToken");
    }

    protected String getRecipientEmailFromConfig() {
        return (String) getConfigAsJson().get("recipientEmail");
    }

    protected String getRecipientIdFromConfig() {
        return (String) getConfigAsJson().get("recipientId");
    }

    private JSONObject getConfigAsJson() {
        StringBuffer stringBuffer = new StringBuffer();
        JSONObject jsonResponse = null;

        jsonResponse = getJsonObjectFromConfig(stringBuffer, jsonResponse);

        return jsonResponse;
    }

    private JSONObject getJsonObjectFromConfig(StringBuffer stringBuffer, JSONObject jsonResponse) {
        String currentLine;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test/java/integration/com/flowdock/jenkins/integration.cfg"));

            while ((currentLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(currentLine);
            }

            jsonResponse = (JSONObject) JSONValue.parse(stringBuffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    public void addFlowdockNotificationToPostBuildActions(FreeStyleProject project, FlowdockNotifier notifierSpy) throws IOException {
        project.getPublishersList().add(notifierSpy);
    }

    public FreeStyleBuild kickOffBuild(FreeStyleProject project) throws InterruptedException, ExecutionException {
        return project.scheduleBuild2(0).get();
    }

    public FakeChangeLogSet getChangeSetWithAuthors(AbstractBuild build, String... authors) {
        List<EntryImpl> entries = new ArrayList<EntryImpl>();
        for (String author : authors) {
            entries.add(getEntryWithAuthor(author));
        }

        return new FakeChangeLogSet(build, entries);
    }

    private EntryImpl getEntryWithAuthor(String author) {
        EntryImpl entry = new EntryImpl();
        entry.withAuthor(author).withMsg("new commit");
        return entry;
    }

    public void test() {
        // This is just a dummy test because JUnit complains about no test cases
        assertThat(true, is(true));
    }
}