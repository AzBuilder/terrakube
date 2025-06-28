package io.terrakube.api.plugin.state.model.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.terrakube.api.plugin.state.model.generic.Resource;

import java.util.Map;

@Getter
@Setter
@ToString
public class ConfigurationModel extends Resource {

    Map<String, Object> attributes;
}
