package com.flowdock.jenkins;

import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.MockBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
    public FlowdockNotifier createFlowdockNotifierSpy(String chatNotification, String privateNotification) {
        FlowdockNotifier notifier = new FlowdockNotifier(
                "123", null, chatNotification, privateNotification, "456", "true", "true", "true", "true", "true", "true");
        FlowdockNotifier notifierSpy = spy(notifier);
        doReturn(mock(FlowdockAPI.class)).when(notifierSpy).getFlowdockAPIForMessage(any(FlowdockMessage.class));

        return notifierSpy;
    }

    public void addFlowdockNotificationToPostBuildActions(FreeStyleProject project, FlowdockNotifier notifierSpy) throws IOException {
        project.getPublishersList().add(notifierSpy);
    }

    public void kickOffBuild(FreeStyleProject project) throws InterruptedException, ExecutionException {
        project.scheduleBuild2(0).get();
    }

    public void test() {
        // This is just a dummy test because JUnit complains about no test cases
        assertThat(true, is(true));
    }
}