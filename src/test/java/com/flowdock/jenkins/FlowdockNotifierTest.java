package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.UnstableBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class FlowdockNotifierTest extends FlowdockTestCase {

    public void testNotifierShouldAlwaysSendTeamInboxMessage() throws Exception {
        FreeStyleProject project = createProject(new UnstableBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy(null, null);

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendTeamInboxMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

    public void testNotifierShouldSendChatMessageWhenEnabledAndBuildDoesNotSucceed() throws Exception {
        FreeStyleProject project = createProject(new FailureBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy("true", null);

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendChatMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

    public void testNotifierShouldSendPrivateMessageWhenEnabledAndBuildDoesNotSucceed() throws Exception {
        FreeStyleProject project = createProject(new FailureBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy(null, "true");

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendPrivateMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }
}
