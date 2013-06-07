package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlowdockMessageTest {

    @Test
    public void getBuildNumberShouldStripHashes() {
        FlowdockMessage message = createFlowdockMessage();
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getDisplayName()).thenReturn("#123#456");
        message.setBuild(build);

        assertThat(message.getBuildNumber(), is("123456"));
    }

    @Test
    public void setTokenShouldTrimWhitespace() {
        FlowdockMessage message = createFlowdockMessage();
        message.setToken(" 123 456 789 ");

        assertThat(message.getToken(), is("123456789"));
    }

    private FlowdockMessage createFlowdockMessage() {
        return new FlowdockMessage() {
            @Override
            public String asPostData() throws UnsupportedEncodingException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void setApiUrl() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            protected void setContentFromBuild(AbstractBuild build, BuildResult buildResult) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
