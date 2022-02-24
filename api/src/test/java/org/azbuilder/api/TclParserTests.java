package org.azbuilder.api;

import org.azbuilder.api.plugin.scheduler.job.tcl.TclService;
import org.azbuilder.api.repository.JobRepository;
import org.azbuilder.api.repository.OrganizationRepository;
import org.azbuilder.api.repository.TemplateRepository;
import org.azbuilder.api.repository.WorkspaceRepository;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.azbuilder.api.rs.template.Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
public class TclParserTests extends ServerApplicationTests{

    @Autowired
    JobRepository jobRepository;

    @Autowired
    TclService tclService;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO template (id, name, description, version, tcl, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb578','plan_apply','sample description', '1.0.0', 'ZmxvdzoKICAtIHR5cGU6ICJ0ZXJyYWZvcm1QbGFuIgogICAgc3RlcDogMTAwCiAgLSB0eXBlOiAidGVycmFmb3JtQXBwbHkiCiAgICBzdGVwOiAyMDA=', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO workspace (id, name, source, branch, terraform_version, organization_id) VALUES\n" +
                    "\t\t('c05da917-81a3-4da3-9619-20b240cbd7f7','Workspace','https://github.com/AzBuilder/terraform-sample-repository.git', 'main', '0.15.2', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');"
    })
    void jobApiGetTest() {
        Job job = new Job();
        job.setWorkspace(workspaceRepository.getById(UUID.fromString("c05da917-81a3-4da3-9619-20b240cbd7f7")));
        job.setOrganization(organizationRepository.getById(UUID.fromString("a42f538b-8c75-4311-8e73-ea2c0f2fb577")));

        Template template = templateRepository.getById(UUID.fromString("a42f538b-8c75-4311-8e73-ea2c0f2fb578"));
        job.setTcl(template.getTcl());
        job.setTemplateReference("a42f538b-8c75-4311-8e73-ea2c0f2fb578");
        job.setStatus(JobStatus.pending);
        job.setCreatedBy("serviceAccount");
        job.setUpdatedBy("serviceAccount");
        Date triggerDate = new Date(System.currentTimeMillis());
        job.setCreatedDate(triggerDate);
        job.setUpdatedDate(triggerDate);

        jobRepository.save(job);
    }
}
