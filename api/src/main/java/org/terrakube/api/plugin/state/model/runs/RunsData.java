package org.terrakube.api.plugin.state.model.runs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RunsData {
    RunsModel data;
    List included;
}
