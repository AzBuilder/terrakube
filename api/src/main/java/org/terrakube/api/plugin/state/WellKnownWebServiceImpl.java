package org.terrakube.api.plugin.state;

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
            "    \"client\": \"12345\",\n" +
            "    \"grant_types\": [\"authz_code\", \"openid\", \"profile\", \"email\", \"offline_access\", \"groups\"],\n" +
            "    \"authz\": \"/auth?scope=openid+profile+email+offline_access+groups\",\n" +
            "    \"token\": \"/token\",\n" +
            "    \"ports\": [10000, 10001]\n" +
            "    }, \n"+
            "  \"state.v2\": \"/terrakube/state/v1/\"\n," +
            "  \"tfe.v2.1\": \"/terrakube/tfe/v1/\"\n" +
            "}";

    @Value("${org.terrakube.hostname}")
    String hostname;

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> terraformJson() {
        return ResponseEntity.ok(terraformJsonContent);
    }
}
