package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import org.junit.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.UnstableBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class FlowdockNotifierTest extends FlowdockTestCase {

    /*
     * FlowdockNotifier should always send a TeamInboxMessage
    */
    public void test2() throws Exception {
        FreeStyleProject project = createProject(new UnstableBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy(null, null);

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendTeamInboxMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

    /*
     * FlowdockNotifier should send a ChatMessage when enabled and build doesn't succeed
     */
    public void test3() throws Exception {
        FreeStyleProject project = createProject(new FailureBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy("true", null);

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendChatMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

    /*
     * FlowdockNotifier should send a PrivateMessage when enabled and build doesn't succeed
     */
    public void test4() throws Exception {
        FreeStyleProject project = createProject(new FailureBuilder());
        FlowdockNotifier notifierSpy = createFlowdockNotifierSpy(null, "true");

        addFlowdockNotificationToPostBuildActions(project, notifierSpy);
        kickOffBuild(project);

        verify(notifierSpy).sendPrivateMessage(any(AbstractBuild.class), any(BuildResult.class), any(BuildListener.class));
    }

}
