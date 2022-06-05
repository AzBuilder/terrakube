package org.azbuilder.executor.service.mode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.azbuilder.executor.service.scripts.ScriptType;

@ToString
@Getter
@Setter
public class Command {
    private ScriptType runtime;
    private String script;
    private int priority;
    private boolean before;
    private boolean after;
}
