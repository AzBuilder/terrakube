package org.terrakube.executor.plugin.tfoutput;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TerraformOutputPathService {

    //{{server}}/tfoutput/v1/organization/{{organizationId}}/job/{{jobId}}/step/{{stepId}}
    private static final String GENERIC_OUTPUT_PATH = "%s/tfoutput/v1/organization/%s/job/%s/step/%s";

    @Value("${org.terrakube.api.url}")
    private String apiUrl;

    public String getOutputPath(String organizationId, String jobId, String stepId){
        return String.format(GENERIC_OUTPUT_PATH, this.apiUrl, organizationId, jobId, stepId);
    }
}
