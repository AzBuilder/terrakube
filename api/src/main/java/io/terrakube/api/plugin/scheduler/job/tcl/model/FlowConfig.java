package io.terrakube.api.plugin.scheduler.job.tcl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class FlowConfig {
    List<Flow> flow;
}