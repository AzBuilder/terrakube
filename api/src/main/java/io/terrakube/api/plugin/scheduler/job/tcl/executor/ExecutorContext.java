package io.terrakube.api.plugin.scheduler.job.tcl.executor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.terrakube.api.plugin.scheduler.job.tcl.model.Command;

import java.util.HashMap;
import java.util.List;

@ToString
@Getter
@Setter
public class ExecutorContext {
    private List<Command> commandList;
    private String type;
    private String organizationId;
    private String workspaceId;
    private String jobId;
    private String stepId;
    private String terraformVersion;
    private String source;
    private String branch;
    private String folder;
    private String vcsType;
    private String connectionType;
    private boolean refresh;
    private boolean refreshOnly;
    private boolean showHeader;
    private boolean ignoreError;
    private String accessToken;
    private String moduleSshKey;
    private String commitId;
    private boolean tofu;
    private String agentUrl;
    private HashMap<String, String> environmentVariables;
    private HashMap<String, String> variables;
}
