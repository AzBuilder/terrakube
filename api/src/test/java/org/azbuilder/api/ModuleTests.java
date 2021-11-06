package org.azbuilder.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import com.yahoo.elide.test.jsonapi.elements.Attribute;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class ModuleTests extends ServerApplicationTests{

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO vcs (id, name, description, vcs_type, organization_id) VALUES\n" +
                    "\t\t('0f21ba16-16d4-4ac7-bce0-3484024ee6bf','publicConnection', 'publicConnection', 'PUBLIC', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO module (id, name, description, provider, source, download_quantity, organization_id, vcs_id) VALUES\n" +
                    "\t\t('b5e41ba0-e7a5-4643-9200-1c45c5b82648','Module','Description', 'Provider', 'https://github.com/AzBuilder/terraform-sample-repository.git', 100, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577', '0f21ba16-16d4-4ac7-bce0-3484024ee6bf');"
    })
    void moduleApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/module")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "module"),
                                        id("b5e41ba0-e7a5-4643-9200-1c45c5b82648"),
                                        attributes(
                                                attr("description", "Description"),
                                                attr("downloadQuantity", 100),
                                                attr("name", "Module"),
                                                attr("provider", "Provider"),
                                                attr("registryPath", "Organization/Module/Provider"),
                                                attr("source", "https://github.com/AzBuilder/terraform-sample-repository.git"),
                                                attr("versions", Arrays.asList("0.0.2","0.0.1"))
                                        ),
                                        relationships(
                                                relation("organization",true,
                                                        resource(
                                                                type("organization"),
                                                                id("a42f538b-8c75-4311-8e73-ea2c0f2fb577")
                                                        )
                                                ),
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
}
