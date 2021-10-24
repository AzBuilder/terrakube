package org.azbuilder.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class VcsTests extends ServerApplicationTests{

    @Test
    @Sql(statements = {
            "DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO vcs (id, name, description, vcs_type, client_id, client_secret, access_token, organization_id) VALUES\n" +
                    "\t\t('0f21ba16-16d4-4ac7-bce0-3484024ee6bf','publicConnection', 'publicConnection', 'PUBLIC', 'sampleId', 'sampleSecret', 'sampleToken', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');"
    })
    void moduleApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/vcs")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "vcs"),
                                        id("0f21ba16-16d4-4ac7-bce0-3484024ee6bf"),
                                        attributes(
                                                attr("accessToken", "sampleToken"),
                                                attr("clientId", "sampleId"),
                                                attr("clientSecret", "sampleSecret"),
                                                attr("description", "publicConnection"),
                                                attr("name", "publicConnection"),
                                                attr("vcsType", "PUBLIC")
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
