package org.terrakube.registry;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class WellKnownTests extends OpenRegistryApplicationTests{

    private static final String WELL_KNOWN_RESPONSE="{\n" +
            "  \"modules.v1\": \"http://localhost/terraform/modules/v1/\"\n," +
            "  \"providers.v1\": \"http://localhost/terraform/providers/v1/\"," +
            "  \"login.v1\": {\n" +
            "    \"client\": \"sample\",\n" +
            "    \"grant_types\": [\"authz_code\", \"sample\"],\n" +
            "    \"authz\": \"https://login.microsoftonline.com/sample/oauth2/v2.0/authorize?scope=sample\",\n" +
            "    \"token\": \"https://login.microsoftonline.com/sample/oauth2/v2.0/token\",\n" +
            "    \"ports\": [10000, 10001]\n" +
            "    }"+
            "}";

    @Test
    void providerApiGetTest() {

        when()
                .get("/.well-known/terraform.json")
                .then()
                .log().all()
                .body(containsString(WELL_KNOWN_RESPONSE))
                .log().all()
                .statusCode(HttpStatus.SC_OK);

    }
}
