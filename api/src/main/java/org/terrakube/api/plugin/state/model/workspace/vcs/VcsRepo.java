package org.terrakube.api.plugin.state.model.workspace.vcs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VcsRepo {

    private String branch;
    @JsonProperty("repository-http-url")
    private String repositoryHttpUrl;
}
