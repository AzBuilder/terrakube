package org.azbuilder.api.schedule.executor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.azbuilder.api.schedule.yaml.Command;

import java.util.HashMap;
import java.util.List;

@ToString
@Getter
@Setter
public class ExecutorJob {
    private List<Command> commandList;
    private String type;
    private String organizationId;
    private String workspaceId;
    private String jobId;
    private String stepId;
    private String terraformVersion;
    private String source;
    private String branch;
    private String vcsType;
    private String accessToken;
    private HashMap<String, String> environmentVariables;
    private HashMap<String, String> variables;
}
