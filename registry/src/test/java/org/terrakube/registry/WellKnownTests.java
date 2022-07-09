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
            "    \"grant_types\": [\"authz_code\", \"openid\", \"profile\", \"email\", \"offline_access\", \"groups\"],\n" +
            "    \"authz\": \"https://sample.com/auth?scope=openid+profile+email+offline_access+groups\",\n" +
            "    \"token\": \"https://sample.com/token\",\n" +
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
