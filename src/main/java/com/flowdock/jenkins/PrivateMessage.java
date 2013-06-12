package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import hudson.model.AbstractBuild;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import static hudson.scm.ChangeLogSet.Entry;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class PrivateMessage extends FlowdockMessage {

    public static final Pattern PAIRING_PATTERN = Pattern.compile("^pairing.*", CASE_INSENSITIVE);
    public static final Pattern FULL_NAME_PATTERN = Pattern.compile("[A-Z]{1}[a-z]+\\s[A-Z]{1}[a-z]+");
    public static final String USER_API_URL = "https://api.flowdock.com/users";

    private String apiUrl;
    private String recipientId;
    private String recipientEmail;
    private String username;
    private String password;

    public PrivateMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String asPostData() throws UnsupportedEncodingException {
        StringBuffer postData = new StringBuffer();
        postData.append("event=").append("message");
        postData.append("&content=").append(urlEncode(content));

        return postData.toString();
    }

    @Override
    public String getApiUrl() {
        return this.apiUrl;
    }

    @Override
    public void setApiUrl() {
        this.apiUrl = MessageFormat.format("https://api.flowdock.com/private/{0}/messages", this.recipientId);
    }

    @Override
    protected void setContentFromBuild(AbstractBuild build, BuildResult buildResult) throws FlowdockException {
        setBuildAndResult(build, buildResult);
        setRecipient();
    }

    private void setRecipient() throws FlowdockException {
        setRecipientEmail(getRallyAuthor(build.getChangeSet()));
        setRecipientId(getUserId());
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
        this.setApiUrl();
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRallyAuthor(ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet) {
        String rallyAuthorString = null;
        for (Entry entry : reverseCommits(changeLogSet)) {
            if (rallyAuthorString != null) {
                break;
            }

            String author = entry.getAuthor().getFullName();
            if (isPairingAlias(author)) {
                rallyAuthorString = getRallyAuthorFromPairingAlias(author);
            } else if (isFullName(author)) {
                rallyAuthorString = getRallyAuthorFromFullName(author);
            } else if (!isEmpty(author)) {
                rallyAuthorString = getRallyAuthorFromInitials(author);
            }
        }

        return rallyAuthorString;
    }

    private boolean isPairingAlias(String author) {
        return PAIRING_PATTERN.matcher(author).matches();
    }

    private boolean isFullName(String author) {
        return FULL_NAME_PATTERN.matcher(author).matches();
    }

    private boolean isEmpty(String author) {
        return author == null  || author.isEmpty();
    }

    private String getRallyAuthorFromPairingAlias(String author) {
        return MessageFormat.format("{0}@rallydev.com", author.split("\\+")[1]);
    }

    private String getRallyAuthorFromFullName(String author) {
        String firstInitial = author.substring(0, 1).toLowerCase();
        String lastName = author.split("\\s")[1].toLowerCase();

        return MessageFormat.format("{0}{1}@rallydev.com", firstInitial, lastName);
    }

    private String getRallyAuthorFromInitials(String author) {
        return MessageFormat.format("{0}@rallydev.com", author);
    }

    public String getUserId() throws FlowdockException {
        String userId = null;
        JSONArray users = getJsonArrayOfUsers();

        for (Object userObject : users) {
            JSONObject user = (JSONObject) userObject;

            if (user.get("email").equals(getRecipientEmail())) {
                userId = user.get("id").toString();
                break;
            }
        }

        return userId;
    }

    private JSONArray getJsonArrayOfUsers() throws FlowdockException {
        FlowdockAPI api = new FlowdockAPI(this);
        Object users = api.getUsers();

        return (JSONArray) users;
    }
}