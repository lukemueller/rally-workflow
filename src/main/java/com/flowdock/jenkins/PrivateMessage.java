package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.User;
import hudson.scm.ChangeLogSet;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hudson.scm.ChangeLogSet.Entry;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class PrivateMessage extends FlowdockMessage {

    public static final Pattern PAIRING_PATTERN = Pattern.compile("^pairing.*", CASE_INSENSITIVE);

    private String apiUrl;
    private String recipient;

    public PrivateMessage(String token) {
        setToken(token);
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
        this.apiUrl = MessageFormat.format("https://{0}@api.flowdock.com/private/{1}/messages", this.token, this.recipient);
    }

    @Override
    protected void setContentFromBuild(AbstractBuild build, BuildResult buildResult) {
        setBuildAndResult(build, buildResult);
    }

    protected String getRallyAuthor(ChangeLogSet<? extends ChangeLogSet.Entry> changes) {
        String rallyAuthorString = null;
        for (Entry entry : changes) {
            User author = entry.getAuthor();

            if (isPairingAlias(author)) {
                rallyAuthorString = getRallyAuthorFromPairingAlias(author);
            }
        }

        return rallyAuthorString;
    }

    private boolean isPairingAlias(User author) {
        return PAIRING_PATTERN.matcher(author.getFullName()).matches();
    }

    private String getRallyAuthorFromPairingAlias(User author) {
        String fullName = author.getFullName();

        return MessageFormat.format("{0}@rallydev.com", fullName.split("\\+")[1]);
    }

    protected void setRecipient(String recipient) {
        this.recipient = recipient;
        this.setApiUrl();
    }

}
