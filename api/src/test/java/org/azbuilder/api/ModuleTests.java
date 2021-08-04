package org.azbuilder.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class ModuleTests extends ServerApplicationTests{

    @Test
    @Sql(statements = {
            "DELETE implementation; DELETE version; DELETE module; DELETE FROM provider; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO module (id, name, description, provider, source, source_sample, organization_id) VALUES\n" +
                    "\t\t('b5e41ba0-e7a5-4643-9200-1c45c5b82648','Module','Description', 'Provider', 'https://github.com/AzBuilder/terraform-sample-repository.git', 'https://github.com/AzBuilder/terraform-sample-repository.git', 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');"
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
                                                attr("name", "Module"),
                                                attr("provider", "Provider"),
                                                attr("registryPath", "Organization/Module/Provider"),
                                                attr("source", "https://github.com/AzBuilder/terraform-sample-repository.git"),
                                                attr("sourceSample", "https://github.com/AzBuilder/terraform-sample-repository.git"),
                                                attr("versions", Arrays.asList("0.0.2","0.0.1"))
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
