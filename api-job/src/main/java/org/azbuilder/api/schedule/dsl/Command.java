package org.azbuilder.api.schedule.dsl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Command {
    private String runtime;
    private String script;
    private boolean before;
    private boolean after;
}
