package org.terrakube.api.plugin.state.model.outputs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StateOutputs {
    List<OutputData> data;
}
