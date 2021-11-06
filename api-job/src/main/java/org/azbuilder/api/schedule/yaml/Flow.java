package org.azbuilder.api.schedule.yaml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class Flow {
    private String type;
    private String team;
    private int step;
    List<Command> commands;
}
