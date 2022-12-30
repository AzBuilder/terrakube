package org.terrakube.api.plugin.state.model.runs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relationships {

    @JsonProperty("configuration-version")
    ConfigurationModel configurationVersion;
    WorkspaceModel workspace;
    PlanModel plan;
}
