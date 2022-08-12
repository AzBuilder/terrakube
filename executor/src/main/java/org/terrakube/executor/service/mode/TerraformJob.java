package org.terrakube.executor.service.mode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

@ToString
@Getter
@Setter
public class TerraformJob {

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
    private String accessToken;
    private String terraformOutput;
    private HashMap<String, String> environmentVariables;
    private HashMap<String, String> variables;

}
