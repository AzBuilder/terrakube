package io.terrakube.api.plugin.state.model.terraform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TerraformState {
    Map<String, Map<String, Object>> outputs;
}
