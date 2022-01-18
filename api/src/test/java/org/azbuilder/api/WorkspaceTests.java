package org.azbuilder.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class WorkspaceTests extends ServerApplicationTests{

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO vcs (id, name, description, vcs_type, organization_id) VALUES\n" +
                    "\t\t('0f21ba16-16d4-4ac7-bce0-3484024ee6bf','publicConnection', 'publicConnection', 'PUBLIC', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO workspace (id, name, source, branch, terraform_version, organization_id, vcs_id, description) VALUES\n" +
                    "\t\t('c05da917-81a3-4da3-9619-20b240cbd7f7','Workspace','https://github.com/AzBuilder/terraform-sample-repository.git', 'main', '0.15.2', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577', '0f21ba16-16d4-4ac7-bce0-3484024ee6bf', 'Description');"
    })
    void workspaceApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/workspace")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "workspace"),
                                        id("c05da917-81a3-4da3-9619-20b240cbd7f7"),
                                        attributes(
                                                attr("branch", "main"),
                                                attr("description", "Description"),
                                                attr("name", "Workspace"),
                                                attr("source", "https://github.com/AzBuilder/terraform-sample-repository.git"),
                                                attr("terraformVersion", "0.15.2")
                                        ),
                                        relationships(
                                                relation("history"),
                                                relation("job"),
                                                relation("organization",true,
                                                        resource(
                                                                type("organization"),
                                                                id("a42f538b-8c75-4311-8e73-ea2c0f2fb577")
                                                        )
                                                ),
                                                relation("schedule"),
                                                relation("variable"),
                                                relation("vcs",true,
                                                        resource(
                                                                type("vcs"),
                                                                id("0f21ba16-16d4-4ac7-bce0-3484024ee6bf")
                                                        )
                                                )
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO workspace (id, name, source, branch, terraform_version, organization_id) VALUES\n" +
                    "\t\t('c05da917-81a3-4da3-9619-20b240cbd7f7','Workspace','https://github.com/AzBuilder/terraform-sample-repository.git', 'main', '0.15.2', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO variable (id, variable_key, variable_value, variable_category, sensitive, workspace_id, variable_description, hcl) VALUES\n" +
                    "\t\t('4ea7855d-ab07-4080-934c-3aab429da889','variableKey','variableValue', 'TERRAFORM', false, 'c05da917-81a3-4da3-9619-20b240cbd7f7', 'someDescription', true);"
    })
    void variableApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/workspace/c05da917-81a3-4da3-9619-20b240cbd7f7/variable")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "variable"),
                                        id("4ea7855d-ab07-4080-934c-3aab429da889"),
                                        attributes(
                                                attr("category", "TERRAFORM"),
                                                attr("description", "someDescription"),
                                                attr("hcl", true),
                                                attr("key", "variableKey"),
                                                attr("sensitive", false),
                                                attr("value", "variableValue")
                                        ),
                                        relationships(
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

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO workspace (id, name, source, branch, terraform_version, organization_id) VALUES\n" +
                    "\t\t('c05da917-81a3-4da3-9619-20b240cbd7f7','Workspace','https://github.com/AzBuilder/terraform-sample-repository.git', 'main', '0.15.2', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO history (id, output, workspace_id) VALUES\n" +
                    "\t\t('4ea7855d-ab07-4080-934c-3aab429da889','sampleOutput', 'c05da917-81a3-4da3-9619-20b240cbd7f7');"
    })
    void stateApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/workspace/c05da917-81a3-4da3-9619-20b240cbd7f7/history")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "history"),
                                        id("4ea7855d-ab07-4080-934c-3aab429da889"),
                                        attributes(
                                                attr("createdBy", null),
                                                attr("createdDate", null),
                                                attr("output", "sampleOutput"),
                                                attr("updatedBy", null),
                                                attr("updatedDate", null)
                                        ),
                                        relationships(
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

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO workspace (id, name, source, branch, terraform_version, organization_id) VALUES\n" +
                    "\t\t('c05da917-81a3-4da3-9619-20b240cbd7f7','Workspace','https://github.com/AzBuilder/terraform-sample-repository.git', 'main', '0.15.2', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO schedule (id, cron, tcl, enabled, description, workspace_id) VALUES\n" +
                    "\t\t('4ea7855d-ab07-4080-934c-3aab429da889','0/30 0/1 * 1/1 * ? *', 'sampleSchedule', true, 'sampleDescription', 'c05da917-81a3-4da3-9619-20b240cbd7f7');"
    })
    void scheduleApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/workspace/c05da917-81a3-4da3-9619-20b240cbd7f7/schedule")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "schedule"),
                                        id("4ea7855d-ab07-4080-934c-3aab429da889"),
                                        attributes(
                                                attr("createdBy", null),
                                                attr("createdDate", null),
                                                attr("cron", "0/30 0/1 * 1/1 * ? *"),
                                                attr("description", "sampleDescription"),
                                                attr("enabled", true),
                                                attr("tcl", "sampleSchedule"),
                                                attr("updatedBy", null),
                                                attr("updatedDate", null)
                                        ),
                                        relationships(
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
}
