package org.azbuilder.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import com.yahoo.elide.test.graphql.GraphQLDSL;
import com.yahoo.elide.spring.controllers.JsonApiController;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import javax.ws.rs.core.MediaType;

import java.util.Arrays;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.attr;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.attributes;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.data;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.datum;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.id;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.linkage;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.relation;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.relationships;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.resource;
import static com.yahoo.elide.test.jsonapi.JsonApiDSL.type;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class OrganizationTests extends ServerApplicationTests {

    @Test
    @Sql(statements = {
            "DELETE implementation; DELETE version; DELETE module; DELETE FROM provider; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');"
    })
    void organizationApiGetTest() {
        when()
                .get("/api/v1/organization")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type("organization"),
                                        id("a42f538b-8c75-4311-8e73-ea2c0f2fb577"),
                                        attributes(
                                                attr("description", "Description"),
                                                attr("name", "Organization")
                                        ),
                                        relationships(
                                                relation("job"),
                                                relation("module"),
                                                relation("provider"),
                                                relation("workspace")
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

}
