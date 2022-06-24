package org.terrakube.executor.plugin.tfoutput;

public interface TerraformOutput {
    String save(String organizationId, String jobId, String stepId, String output, String outputError);
}
