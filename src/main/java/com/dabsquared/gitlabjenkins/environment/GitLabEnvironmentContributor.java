package com.dabsquared.gitlabjenkins.environment;

import com.dabsquared.gitlabjenkins.cause.GitLabWebHookCause;
import hudson.EnvVars;
import hudson.Extension;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.EnvironmentContributor;
import hudson.model.Run;
import hudson.model.TaskListener;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.IOException;

/**
 * @author Robin Müller
 */
@Extension
public class GitLabEnvironmentContributor extends EnvironmentContributor {
    @Override
    public void buildEnvironmentFor(@NonNull Run r, @NonNull EnvVars envs, @NonNull TaskListener listener) throws IOException, InterruptedException {
        GitLabWebHookCause cause = null;
        if (r instanceof MatrixRun) {
            MatrixBuild parent = ((MatrixRun)r).getParentBuild();
            if (parent != null) {
                cause = (GitLabWebHookCause) parent.getCause(GitLabWebHookCause.class);
            }
        } else {
            cause = (GitLabWebHookCause) r.getCause(GitLabWebHookCause.class);
        }
        if (cause != null) {
            envs.overrideAll(cause.getData().getBuildVariables());
        }
    }
}
