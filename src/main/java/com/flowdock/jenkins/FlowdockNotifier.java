package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FlowdockNotifier extends Notifier {


    private final String flowToken;
    private final String notificationTags;
    private final boolean chatNotification;
    private final boolean privateNotification;
    private final String username;
    private final String password;

    private final Map<BuildResult, Boolean> notifyMap = new HashMap<BuildResult, Boolean>();
    private final boolean notifySuccess;
    private final boolean notifyFailure;
    private final boolean notifyFixed;
    private final boolean notifyUnstable;
    private final boolean notifyAborted;
    private final boolean notifyNotBuilt;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public FlowdockNotifier(String flowToken, String notificationTags, String chatNotification,
                            String privateNotification, String username, String password,
                            String notifySuccess, String notifyFailure, String notifyFixed,
                            String notifyUnstable, String notifyAborted, String notifyNotBuilt) {
        this.flowToken = flowToken;
        this.notificationTags = notificationTags;
        this.privateNotification = toBoolean(privateNotification);
        this.username = username;
        this.password = password;
        this.chatNotification = toBoolean(chatNotification);

        this.notifySuccess = toBoolean(notifySuccess);
        this.notifyFailure = toBoolean(notifyFailure);
        this.notifyFixed = toBoolean(notifyFixed);
        this.notifyUnstable = toBoolean(notifyUnstable);
        this.notifyAborted = toBoolean(notifyAborted);
        this.notifyNotBuilt = toBoolean(notifyNotBuilt);
        setNotifyMap();
    }

    private boolean toBoolean(String configParameter) {
        return configParameter != null && configParameter.equals("true");
    }

    private void setNotifyMap() {
        this.notifyMap.put(BuildResult.SUCCESS, notifySuccess);
        this.notifyMap.put(BuildResult.FAILURE, notifyFailure);
        this.notifyMap.put(BuildResult.FIXED, notifyFixed);
        this.notifyMap.put(BuildResult.UNSTABLE, notifyUnstable);
        this.notifyMap.put(BuildResult.ABORTED, notifyAborted);
        this.notifyMap.put(BuildResult.NOT_BUILT, notifyNotBuilt);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        BuildResult buildResult = BuildResult.fromBuild(build);
        if (shouldNotify(buildResult)) {
            notifyFlowdock(build, buildResult, listener);
        } else {
            listener.getLogger().println("No Flowdock notification configured for build status: " + buildResult.toString());
        }
        return true;
    }

    public boolean shouldNotify(BuildResult buildResult) {
        return notifyMap.get(buildResult);
    }

    protected void notifyFlowdock(AbstractBuild build, BuildResult buildResult, BuildListener listener) {
        try {
            sendTeamInboxMessage(build, buildResult, listener);
            if (buildDidNotSucceed(build) && chatNotification) {
                sendChatMessage(build, buildResult, listener);
            }
            if (buildDidNotSucceed(build) && privateNotification) {
                sendPrivateMessage(build, buildResult, listener);
            }
        } catch (FlowdockException e) {
            listener.getLogger().println("Flowdock: failed to send notification");
            listener.getLogger().println("Flowdock: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            listener.getLogger().println("Flowdock: " + e.getMessage());
        }
    }

    protected void sendTeamInboxMessage(AbstractBuild build, BuildResult buildResult, BuildListener listener) throws FlowdockException, UnsupportedEncodingException {
        TeamInboxMessage teamInboxMessage = new TeamInboxMessage(flowToken);
        buildAndSendMessage(build, buildResult, teamInboxMessage);
        listener.getLogger().println("Flowdock: Team Inbox notification sent successfully");
    }

    protected void sendChatMessage(AbstractBuild build, BuildResult buildResult, BuildListener listener) throws FlowdockException, UnsupportedEncodingException {
        ChatMessage chatMessage = new ChatMessage(flowToken);
        buildAndSendMessage(build, buildResult, chatMessage);
        listener.getLogger().println("Flowdock: Chat notification sent successfully");
    }

    protected void sendPrivateMessage(AbstractBuild build, BuildResult buildResult, BuildListener listener) throws FlowdockException, UnsupportedEncodingException {
        PrivateMessage privateMessage = new PrivateMessage(username, password);
        buildAndSendMessage(build, buildResult, privateMessage);
        listener.getLogger().println("Flowdock: Private notification sent successfully");
    }

    protected void buildAndSendMessage(AbstractBuild build, BuildResult buildResult, FlowdockMessage message) throws FlowdockException, UnsupportedEncodingException {
        message.setTags(notificationTags);
        message.setContentFromBuild(build, buildResult);
        FlowdockAPI api = getFlowdockAPIForMessage(message);
        api.sendMessage();
    }

    protected FlowdockAPI getFlowdockAPIForMessage(FlowdockMessage message) {
        return new FlowdockAPI(message);
    }

    private boolean buildDidNotSucceed(AbstractBuild build) {
        return build.getResult() != Result.SUCCESS;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Flowdock notification";
        }

        public FormValidation doTestConnection(@QueryParameter("flowToken") final String flowToken,
                                               @QueryParameter("notificationTags") final String notificationTags) {
            try {
                ChatMessage chatMessage = new ChatMessage(flowToken);
                chatMessage.setTags(notificationTags);
                chatMessage.setContent("Your plugin is ready!");
                FlowdockAPI api = new FlowdockAPI(chatMessage);
                api.sendMessage();

                return FormValidation.ok("Success! Flowdock plugin can send notifications to your flow.");
            } catch (FlowdockException e) {
                return FormValidation.error(e.getMessage());
            } catch (UnsupportedEncodingException e) {
                return FormValidation.error(e.getMessage());
            }
        }
    }
}
