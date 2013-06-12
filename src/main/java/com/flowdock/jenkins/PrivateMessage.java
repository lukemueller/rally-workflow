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

            User author = entry.getAuthor();
            if (isPairingAlias(author)) {
                rallyAuthorString = getRallyAuthorFromPairingAlias(author);
            } else if (isFullName(author)) {
                rallyAuthorString = getRallyAuthorFromFullName(author);
            } else if (authorIsNotEmpty(author)) {
                rallyAuthorString = getRallyAuthorFromInitials(author);
            }
        }

        return rallyAuthorString;
    }

    private String getRallyAuthorFromFullName(User author) {
        String authorName = author.getFullName();
        String firstInitial = authorName.substring(0, 1).toLowerCase();
        String lastName = authorName.split("\\s")[1].toLowerCase();

        return MessageFormat.format("{0}{1}@rallydev.com", firstInitial, lastName);
    }

    private boolean authorIsNotEmpty(User author) {
        return author != null && author.getDisplayName() != null && !author.getDisplayName().isEmpty();
    }

    private String getRallyAuthorFromInitials(User author) {
        return MessageFormat.format("{0}@rallydev.com", author.getDisplayName());
    }

    private boolean isPairingAlias(User author) {
        return PAIRING_PATTERN.matcher(author.getFullName()).matches();
    }

    private boolean isFullName(User author) {
        return FULL_NAME_PATTERN.matcher(author.getFullName()).matches();
    }

    private String getRallyAuthorFromPairingAlias(User author) {
        String fullName = author.getFullName();

        return MessageFormat.format("{0}@rallydev.com", fullName.split("\\+")[1]);
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
