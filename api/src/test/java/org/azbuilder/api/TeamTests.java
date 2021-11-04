package org.azbuilder.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class TeamTests extends ServerApplicationTests{

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, manage_vcs, manage_template, organization_id) VALUES\n" +
                    "\t\t('b5e41ba0-e7a5-4643-9200-1c45c5b82648','sample_team', true, true, true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            })
    void moduleApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/team")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "team"),
                                        id("b5e41ba0-e7a5-4643-9200-1c45c5b82648"),
                                        attributes(
                                                attr("manageModule", true),
                                                attr("manageProvider", true),
                                                attr("manageTemplate", true),
                                                attr("manageVcs", true),
                                                attr("manageWorkspace", true),
                                                attr("name", "sample_team")
                                        ),
                                        relationships(
                                                relation("organization",true,
                                                        resource(
                                                                type("organization"),
                                                                id("a42f538b-8c75-4311-8e73-ea2c0f2fb577")
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
