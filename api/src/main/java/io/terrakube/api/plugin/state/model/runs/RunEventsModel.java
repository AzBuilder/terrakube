package io.terrakube.api.plugin.state.model.runs;

import lombok.Getter;
import lombok.Setter;
import io.terrakube.api.plugin.state.model.generic.Resource;

import java.util.List;

@Getter
@Setter
public class RunEventsModel {

    List<Resource> data;
}
