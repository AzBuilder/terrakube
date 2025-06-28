package io.terrakube.api.plugin.state.model.generic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Resource {
    String type;
    String id;
}