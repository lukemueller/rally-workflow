package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FlowdockAPI {

    private FlowdockMessage message;

    public FlowdockAPI(FlowdockMessage message) {
        this.message = message;
    }

    public void sendMessage() throws FlowdockException, UnsupportedEncodingException {
        String flowdockUrl = getMessageApiUrl();
        String data = getMessageData();
        try {
            // create connection
            URL url = new URL(flowdockUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // send the request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();

            if (connection.getResponseCode() != 200) {
                StringBuffer responseContent = new StringBuffer();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        responseContent.append(responseLine);
                    }
                    in.close();
                } catch (Exception e) {
                    // nothing we can do about this
                } finally {
                    throw new FlowdockException("Flowdock returned an error response with status " +
                            connection.getResponseCode() + " " + connection.getResponseMessage() + ", " +
                            responseContent.toString() + "\n\nURL: " + flowdockUrl);
                }
            }
        } catch (MalformedURLException e) {
            throw new FlowdockException("Flowdock API URL is invalid: " + flowdockUrl);
        } catch (ProtocolException e) {
            throw new FlowdockException("ProtocolException in connecting to Flowdock: " + e.getMessage());
        } catch (IOException e) {
            throw new FlowdockException("IOException in connecting to Flowdock: " + e.getMessage());
        }
    }

    private String getMessageData() throws UnsupportedEncodingException {
        return this.message.asPostData();
    }

    public String getMessageApiUrl() {
        return this.message.getApiUrl();
    }
}
