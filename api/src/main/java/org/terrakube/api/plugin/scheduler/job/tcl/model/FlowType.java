package org.terrakube.api.plugin.scheduler.job.tcl.model;

public enum FlowType {
    terraformPlan,
    terraformPlanDestroy,
    terraformApply,
    terraformDestroy,
    customScripts,
    approval,

    yamlError
}
