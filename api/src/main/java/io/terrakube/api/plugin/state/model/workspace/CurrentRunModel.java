package io.terrakube.api.plugin.state.model.workspace;

import lombok.Getter;
import lombok.Setter;
import io.terrakube.api.plugin.state.model.generic.Resource;

@Getter
@Setter
public class CurrentRunModel {
    Resource data;
}
