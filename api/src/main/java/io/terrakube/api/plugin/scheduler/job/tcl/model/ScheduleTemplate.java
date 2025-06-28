package io.terrakube.api.plugin.scheduler.job.tcl.model;

import lombok.Setter;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ScheduleTemplate {

    private String name;
    private String schedule;
}
