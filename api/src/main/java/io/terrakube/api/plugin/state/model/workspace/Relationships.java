package io.terrakube.api.plugin.state.model.workspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Relationships {

    @JsonProperty("current-run")
    CurrentRunModel currentRun;
}
