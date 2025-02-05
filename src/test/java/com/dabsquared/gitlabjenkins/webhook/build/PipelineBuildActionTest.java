package com.dabsquared.gitlabjenkins.webhook.build;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.dabsquared.gitlabjenkins.GitLabPushTrigger;
import com.dabsquared.gitlabjenkins.gitlab.hook.model.PipelineHook;
import hudson.model.FreeStyleProject;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Milena Zachow
 */
@RunWith(MockitoJUnitRunner.class)
public class PipelineBuildActionTest {

    @ClassRule
    public static JenkinsRule jenkins = new JenkinsRule();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private StaplerResponse response;

    @Mock
    private GitLabPushTrigger trigger;

    FreeStyleProject testProject;

    @Before
    public void setUp() throws IOException{
        testProject = jenkins.createFreeStyleProject();
        testProject.addTrigger(trigger);
    }

    @Test
    public void buildOnSuccess () throws IOException {
        exception.expect(HttpResponses.HttpResponseException.class);
        new PipelineBuildAction(testProject, getJson("PipelineEvent.json"), null).execute(response);

        verify(trigger).onPost(any(PipelineHook.class));
    }

    @Test
    public void doNotBuildOnFailure() throws IOException {
        exception.expect(HttpResponses.HttpResponseException.class);
        new PipelineBuildAction(testProject, getJson("PipelineFailureEvent.json"), null).execute(response);

        verify(trigger, never()).onPost(any(PipelineHook.class));
    }

    private String getJson(String name) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(name));
    }
}
