package org.terrakube.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class JobTests extends ServerApplicationTests{

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO workspace (id, name, source, branch, terraform_version, organization_id) VALUES\n" +
                    "\t\t('c05da917-81a3-4da3-9619-20b240cbd7f7','Workspace','https://github.com/AzBuilder/terraform-sample-repository.git', 'main', '0.15.2', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO job (tcl, template_reference, status, output, workspace_id, organization_id, comments) VALUES\n" +
                    "\t\t('sampleTcl', 'c05da917-81a3-4da3-9619-20b240cbd7f7', 'pending', 'sampleOutput', 'c05da917-81a3-4da3-9619-20b240cbd7f7', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577', 'empty');"
    })
    void jobApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/job")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "job"),
                                        id("1"),
                                        attributes(
                                                attr("approvalTeam", null),
                                                attr("comments", "empty"),
                                                attr("createdBy", null),
                                                attr("createdDate", null),
                                                attr("output", "sampleOutput"),
                                                attr("status", "pending"),
                                                attr("tcl", "sampleTcl"),
                                                attr("templateReference", "c05da917-81a3-4da3-9619-20b240cbd7f7"),
                                                attr("terraformPlan", null),
                                                attr("updatedBy", null),
                                                attr("updatedDate", null)
                                        ),
                                        relationships(
                                                relation("organization",true,
                                                        resource(
                                                                type("organization"),
                                                                id("a42f538b-8c75-4311-8e73-ea2c0f2fb577")
                                                        )
                                                ),
                                                relation("step"),
                                                relation("workspace",true,
                                                        resource(
                                                                type("workspace"),
                                                                id("c05da917-81a3-4da3-9619-20b240cbd7f7")
                                                        )
                                                )
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    /*
    @Test
    @Sql(statements = {
            "DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO workspace (id, name, source, branch, terraform_version, organization_id) VALUES\n" +
                    "\t\t('c05da917-81a3-4da3-9619-20b240cbd7f7','Workspace','https://github.com/AzBuilder/terraform-sample-repository.git', 'main', '0.15.2', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO job ( tcl, status, output, workspace_id, organization_id) VALUES\n" +
                    "\t\t( 'sampleTcl', 'pending', 'sampleOutput', 'c05da917-81a3-4da3-9619-20b240cbd7f7', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');" +
            "INSERT INTO step (id, step_number, status, job_id) " +
                    "SELECT 'a42f538b-8c75-4311-8e73-ea2c0f2fb577', '100', 'completed', ID from job;"
    })
    void stepApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/job/0/step")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "step"),
                                        id("a42f538b-8c75-4311-8e73-ea2c0f2fb577"),
                                        attributes(
                                                attr("status", "completed"),
                                                attr("step_number", 100)
                                        ),
                                        relationships(
                                                relation("job",true,
                                                        resource(
                                                                type("job"),
                                                                id(0)
                                                        )
                                                )
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }*/
}
