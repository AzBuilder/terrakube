package org.azbuilder.api.dsl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class FlowConfig {
    List<Command> flow;
}
