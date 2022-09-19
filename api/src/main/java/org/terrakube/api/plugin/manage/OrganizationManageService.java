package org.terrakube.api.plugin.manage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.repository.TemplateRepository;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.template.Template;

import java.util.Base64;
import java.util.UUID;

@AllArgsConstructor
@Service
public class OrganizationManageService {

    private static String TEMPLATE_PLAN ="flow:\n" +
            "  - type: \"terraformPlan\"\n" +
            "    name: \"Running Terraform Plan\"\n" +
            "    step: 100" +
            "";

    private static String TEMPLATE_APPLY ="flow:\n" +
            "  - type: \"terraformPlan\"\n" +
            "    name: \"Running Terraform Plan\"\n" +
            "    step: 100\n" +
            "  - type: \"terraformApply\"\n" +
            "    name: \"Running Terraform Apply\"\n" +
            "    step: 200" +
            "";

    private static String TEMPLATE_DESTROY ="flow:\n" +
            "  - type: \"terraformDestroy\"\n" +
            "    name: \"Running Terraform Destroy\"\n" +
            "    step: 100" +
            "";

    TemplateRepository templateRepository;

    @Transactional
    public void postCreationSetup(Organization organization){
        templateRepository.save(generateTemplate("Terraform-Plan", "Running Terraform plan", Base64.getEncoder().encodeToString(TEMPLATE_PLAN.getBytes()), organization));
        templateRepository.save(generateTemplate("Terraform-Plan/Apply", "Running Terraform plan and apply", Base64.getEncoder().encodeToString(TEMPLATE_APPLY.getBytes()), organization));
        templateRepository.save(generateTemplate("Terraform-Destroy", "Running Terraform destroy", Base64.getEncoder().encodeToString(TEMPLATE_DESTROY.getBytes()), organization));
    }

    private Template generateTemplate(String name, String description, String tcl, Organization organization){
        Template template = new Template();
        template.setId(UUID.randomUUID());
        template.setName(name);
        template.setDescription(description);
        template.setVersion("1.0.0");
        template.setTcl(tcl);
        template.setOrganization(organization);
        return template;
    }
}
