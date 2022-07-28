package org.terrakube.executor.plugin.tfoutput;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TerraformOutputPathService {

    private static final String GENERIC_OUTPUT_PATH = "%s/tfoutput/v1/organization/%s/job/%s/step/%s";

    @Value("${org.terrakube.api.url}")
    private String apiUrl;

    public String getOutputPath(String organizationId, String jobId, String stepId){
        return String.format(GENERIC_OUTPUT_PATH, this.apiUrl, organizationId, jobId, stepId);
    }
}
