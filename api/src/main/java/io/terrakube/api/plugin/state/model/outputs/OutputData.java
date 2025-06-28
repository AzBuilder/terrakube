package io.terrakube.api.plugin.state.model.outputs;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class OutputData {

    String id;
    String type;
    Map<String, Object> attributes;
}
