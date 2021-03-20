package org.azbuilder.api.variable.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VariableDTO {

    private String variableId;
    private String name;
    private String value;
}
