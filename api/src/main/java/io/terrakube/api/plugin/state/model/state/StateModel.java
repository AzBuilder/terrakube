package io.terrakube.api.plugin.state.model.state;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

import io.terrakube.api.plugin.state.model.generic.Resource;

@Getter
@Setter
@ToString
public class StateModel  extends Resource {
    Map<String, Object> attributes;
}
