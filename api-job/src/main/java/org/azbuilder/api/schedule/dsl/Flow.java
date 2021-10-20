package org.azbuilder.api.schedule.dsl;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter
@Setter
public class Flow {
    private String type;
    private int step;
    LinkedList<Command> commands;
}
