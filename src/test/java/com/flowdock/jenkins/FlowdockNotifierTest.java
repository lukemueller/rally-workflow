package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.MockBuilder;
import org.jvnet.hudson.test.UnstableBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FlowdockNotifierTest extends HudsonTestCase {

    /*
     * FlowdockNotifier should always send a TeamInboxMessage
    */
    public void test1() throws Exception {
        FreeStyleProject project = createProject(new UnstableBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy(null, null);

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendTeamInboxMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

    /*
     * FlowdockNotifier should send a ChatMessage when enabled and build doesn't succeed
     */
    public void test2() throws Exception {
        FreeStyleProject project = createProject(new FailureBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy("true", null);

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendChatMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

    /*
     * FlowdockNotifier should send a PrivateMessage when enabled and build doesn't succeed
     */
    public void test3() throws Exception {
        FreeStyleProject project = createProject(new FailureBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy(null, "true");

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendPrivateMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

    private FreeStyleProject createProject(MockBuilder builder) throws IOException {
        FreeStyleProject project = createFreeStyleProject();
        project.getBuildersList().add(builder);

        return project;
    }

    /*
     * Use Mockito to spy on a FlowdockNotifier instance while stubbing out FlowdockAPI so were not actually
     * sending requests to FlowDock
     */
    private FlowdockNotifier createFlowdockNotifierSpy(String chatNotification, String privateNotification) {
        FlowdockNotifier notifier = new FlowdockNotifier(
                "123", null, chatNotification, privateNotification, "456", "true", "true", "true", "true", "true", "true");
        FlowdockNotifier notifierSpy = spy(notifier);
        doReturn(mock(FlowdockAPI.class)).when(notifierSpy).getFlowdockAPIForMessage(any(FlowdockMessage.class));

        return notifierSpy;
    }

    private void addFlowdockNotificationToPostBuildActions(FreeStyleProject project, FlowdockNotifier notifierSpy) throws IOException {
        project.getPublishersList().add(notifierSpy);
    }

    private void kickOffBuild(FreeStyleProject project) throws InterruptedException, ExecutionException {
        project.scheduleBuild2(0).get();
    }

}
