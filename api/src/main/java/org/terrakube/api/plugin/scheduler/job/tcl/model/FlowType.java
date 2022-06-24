package org.terrakube.api.plugin.scheduler.job.tcl.model;

public enum FlowType {
    terraformPlan,
    terraformApply,
    terraformDestroy,
    customScripts,
    approval
}
