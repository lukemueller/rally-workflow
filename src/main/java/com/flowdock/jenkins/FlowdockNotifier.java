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
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FlowdockNotifier extends Notifier {


    private final String flowToken;
    private final String notificationTags;
    private String privateSenderToken;
    private final boolean chatNotification;
    private final boolean privateNotification;

    private final Map<BuildResult, Boolean> notifyMap;
    private final boolean notifySuccess;
    private final boolean notifyFailure;
    private final boolean notifyFixed;
    private final boolean notifyUnstable;
    private final boolean notifyAborted;
    private final boolean notifyNotBuilt;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public FlowdockNotifier(String flowToken, String notificationTags, String chatNotification,
                            String privateNotification, String privateSenderToken, String notifySuccess,
                            String notifyFailure, String notifyFixed, String notifyUnstable,
                            String notifyAborted, String notifyNotBuilt) {
        this.flowToken = flowToken;
        this.notificationTags = notificationTags;
        this.privateSenderToken = privateSenderToken;
        this.privateNotification = toBoolean(privateNotification);
        this.chatNotification = toBoolean(chatNotification);

        this.notifySuccess = toBoolean(notifySuccess);
        this.notifyFailure = toBoolean(notifyFailure);
        this.notifyFixed = toBoolean(notifyFixed);
        this.notifyUnstable = toBoolean(notifyUnstable);
        this.notifyAborted = toBoolean(notifyAborted);
        this.notifyNotBuilt = toBoolean(notifyNotBuilt);

        // set notification map
        this.notifyMap = new HashMap<BuildResult, Boolean>();
        this.notifyMap.put(BuildResult.SUCCESS, this.notifySuccess);
        this.notifyMap.put(BuildResult.FAILURE, this.notifyFailure);
        this.notifyMap.put(BuildResult.FIXED, this.notifyFixed);
        this.notifyMap.put(BuildResult.UNSTABLE, this.notifyUnstable);
        this.notifyMap.put(BuildResult.ABORTED, this.notifyAborted);
        this.notifyMap.put(BuildResult.NOT_BUILT, this.notifyNotBuilt);
    }

    private boolean toBoolean(String configParameter) {
        return configParameter != null && configParameter.equals("true");
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
            sendChatMessage(build, buildResult, listener);
            sendPrivateMessage(build, buildResult, listener);
        } catch (FlowdockException e) {
            listener.getLogger().println("Flowdock: failed to send notification");
            listener.getLogger().println("Flowdock: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            listener.getLogger().println("Flowdock: " + e.getMessage());
        }
    }

    protected void sendTeamInboxMessage(AbstractBuild build, BuildResult buildResult, BuildListener listener) throws FlowdockException, UnsupportedEncodingException {
        TeamInboxMessage teamInboxMessage = new TeamInboxMessage(flowToken);
        teamInboxMessage.setTags(notificationTags);
        teamInboxMessage.setContentFromBuild(build, buildResult);
        FlowdockAPI api = new FlowdockAPI(teamInboxMessage);
        api.sendMessage();
        listener.getLogger().println("Flowdock: Team Inbox notification sent successfully");
    }

    protected void sendChatMessage(AbstractBuild build, BuildResult buildResult, BuildListener listener) throws FlowdockException, UnsupportedEncodingException {
        if (buildDidNotSucceed(build) && chatNotification) {
            ChatMessage chatMessage = new ChatMessage(flowToken);
            chatMessage.setTags(notificationTags);
            chatMessage.setContentFromBuild(build, buildResult);
            FlowdockAPI api = new FlowdockAPI(chatMessage);
            api.sendMessage();
            listener.getLogger().println("Flowdock: Chat notification sent successfully");
        }
    }

    protected void sendPrivateMessage(AbstractBuild build, BuildResult buildResult, BuildListener listener) throws UnsupportedEncodingException, FlowdockException {
        if (buildDidNotSucceed(build) && privateNotification) {
            PrivateMessage privateMessage = new PrivateMessage(privateSenderToken, "30060");
            privateMessage.setTags(notificationTags);
            privateMessage.setContentFromBuild(build, buildResult);
            FlowdockAPI api = new FlowdockAPI(privateMessage);
            api.sendMessage();
            listener.getLogger().println("Flowdock: Private notification sent successfully");
        }
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

        private String apiUrl = "https://api.flowdock.com";

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Flowdock notification";
        }

//        public FormValidation doTestConnection(@QueryParameter("flowToken") final String flowToken,
//            @QueryParameter("notificationTags") final String notificationTags) {
//            try {
//                FlowdockAPI api = new FlowdockAPI(apiUrl(), flowToken);
//                ChatMessage testMsg = new ChatMessage();
//                testMsg.setTags(notificationTags);
//                testMsg.setContent("Your plugin is ready!");
//                api.pushChatMessage(testMsg);
//                return FormValidation.ok("Success! Flowdock plugin can send notifications to your flow.");
//            } catch(FlowdockException ex) {
//                return FormValidation.error(ex.getMessage());
//            }
//        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            apiUrl = formData.getString("apiUrl");
            save();
            return super.configure(req, formData);
        }

        public String apiUrl() {
            return apiUrl;
        }
    }
}
