package io.terrakube.api.plugin.scheduler.job.tcl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
@Setter
public class ImportComands {
    String repository;
    String folder;
    String branch;
    Map<String, String> inputsEnv;
    Map<String, String> inputsTerraform;
}
