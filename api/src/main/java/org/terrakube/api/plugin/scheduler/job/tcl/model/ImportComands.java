package org.terrakube.api.plugin.scheduler.job.tcl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

@ToString
@Getter
@Setter
public class ImportComands {
    String repository;
    String folder;
    String branch;
    HashMap<String, String> inputsEnv;
    HashMap<String, String> inputsTerraform;
}
