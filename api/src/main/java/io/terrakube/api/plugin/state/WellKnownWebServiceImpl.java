package io.terrakube.api.plugin.state;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/.well-known/terraform.json")
public class WellKnownWebServiceImpl {

    private static final String terraformJsonContent = "{\n" +
            "  \"login.v1\": {\n" +
            "    \"client\": \"%s\",\n" +
            "    \"grant_types\": [\"authz_code\", \"openid\", \"profile\", \"email\", \"offline_access\", \"groups\"],\n" +
            "    \"authz\": \"%s/auth?scope=openid+profile+email+offline_access+groups\",\n" +
            "    \"token\": \"%s/token\",\n" +
            "    \"ports\": [10000, 10001]\n" +
            "    }, \n" +
            "  \"state.v2\": \"/remote/state/v2/\"\n," +
            "  \"tfe.v2\": \"/remote/tfe/v2/\"\n," +
            "  \"tfe.v2.1\": \"/remote/tfe/v2/\"\n" +
            "}";

    @Value("${io.terrakube.token.client-id}")
    String dexClientId;

    @Value("${io.terrakube.token.issuer-uri}")
    String dexIssuerUri;

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> terraformJson() {
        return ResponseEntity.ok(String.format(terraformJsonContent, dexClientId, dexIssuerUri, dexIssuerUri));
    }
}
