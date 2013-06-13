package integration.com.flowdock.jenkins;

import com.flowdock.jenkins.BuildResult;
import com.flowdock.jenkins.FlowdockAPI;
import com.flowdock.jenkins.FlowdockMessage;
import com.flowdock.jenkins.FlowdockNotifier;
import com.flowdock.jenkins.exception.FlowdockException;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.MockBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
     * Use Mockito to spy on a FlowdockNotifier instance while stubbing out FlowdockAPI so were not actually
     * sending requests to FlowDock
     */
    public FlowdockNotifier createFlowdockNotifierSpy(String chatNotification, String privateNotification) throws UnsupportedEncodingException, FlowdockException {
        FlowdockNotifier notifier = new FlowdockNotifier(
                "123", null, chatNotification, privateNotification, null, "true", "true", "true", "true", "true", "true");
        FlowdockNotifier notifierSpy = spy(notifier);
        doReturn(mock(FlowdockAPI.class)).when(notifierSpy).getFlowdockAPIForMessage(any(FlowdockMessage.class));
        doNothing().when(notifierSpy).buildAndSendMessage(any(AbstractBuild.class), any(BuildResult.class), any(FlowdockMessage.class));

        return notifierSpy;
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