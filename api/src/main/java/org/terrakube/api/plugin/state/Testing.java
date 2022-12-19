package org.terrakube.api.plugin.state;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/terrakube/tfe/v1")
public class Testing {

    String response1 = "{\n" +
            "  \"data\": {\n" +
            "    \"id\": \"org-sample\",\n" +
            "    \"type\": \"entitlement-sets\",\n" +
            "    \"attributes\": {\n" +
            "      \"operations\": true,\n" +
            "      \"private-module-registry\": true,\n" +
            "      \"sentinel\": false,\n" +
            "      \"run-tasks\": false,\n" +
            "      \"state-storage\": true,\n" +
            "      \"teams\": false,\n" +
            "      \"vcs-integrations\": true,\n" +
            "      \"usage-reporting\": false,\n" +
            "      \"user-limit\": 5,\n" +
            "      \"self-serve-billing\": true,\n" +
            "      \"audit-logging\": false,\n" +
            "      \"agents\": false,\n" +
            "      \"sso\": false\n" +
            "    }\n" +
            "  }\n" +
            "}";

    String response2 = "{\n" +
            "  \"data\": {\n" +
            "    \"id\": \"sample\",\n" +
            "    \"type\": \"organizations\",\n" +
            "    \"attributes\": {\n" +
            "      \"external-id\": \"org-WV6DfwfxxXvLfvfs\",\n" +
            "      \"created-at\": \"2020-03-26T22:13:38.456Z\",\n" +
            "      \"email\": \"user@example.com\",\n" +
            "      \"session-timeout\": null,\n" +
            "      \"session-remember\": null,\n" +
            "      \"collaborator-auth-policy\": \"password\",\n" +
            "      \"plan-expired\": false,\n" +
            "      \"plan-expires-at\": null,\n" +
            "      \"plan-is-trial\": false,\n" +
            "      \"plan-is-enterprise\": false,\n" +
            "      \"cost-estimation-enabled\": false,\n" +
            "      \"send-passing-statuses-for-untriggered-speculative-plans\": false,\n" +
            "      \"allow-force-delete-workspaces\": false,\n" +
            "      \"name\": \"hashicorp\",\n" +
            "      \"permissions\": {\n" +
            "        \"can-update\": true,\n" +
            "        \"can-destroy\": true,\n" +
            "        \"can-access-via-teams\": true,\n" +
            "        \"can-create-module\": true,\n" +
            "        \"can-create-team\": false,\n" +
            "        \"can-create-workspace\": true,\n" +
            "        \"can-manage-users\": true,\n" +
            "        \"can-manage-subscription\": true,\n" +
            "        \"can-manage-sso\": false,\n" +
            "        \"can-update-oauth\": true,\n" +
            "        \"can-update-sentinel\": false,\n" +
            "        \"can-update-ssh-keys\": true,\n" +
            "        \"can-update-api-token\": true,\n" +
            "        \"can-traverse\": true,\n" +
            "        \"can-start-trial\": true,\n" +
            "        \"can-update-agent-pools\": false,\n" +
            "        \"can-manage-tags\": true,\n" +
            "        \"can-manage-public-modules\": true,\n" +
            "        \"can-manage-public-providers\": false,\n" +
            "        \"can-manage-run-tasks\": false,\n" +
            "        \"can-read-run-tasks\": false,\n" +
            "        \"can-create-provider\": false\n" +
            "      },\n" +
            "      \"fair-run-queuing-enabled\": true,\n" +
            "      \"saml-enabled\": false,\n" +
            "      \"owners-team-saml-role-id\": null,\n" +
            "      \"two-factor-conformant\": false,\n" +
            "      \"assessments-enforced\": false\n" +
            "    },\n" +
            "    \"relationships\": {\n" +
            "      \"oauth-tokens\": {\n" +
            "        \"links\": {\n" +
            "          \"related\": \"/api/v2/organizations/sample/oauth-tokens\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"authentication-token\": {\n" +
            "        \"links\": {\n" +
            "          \"related\": \"/api/v2/organizations/sample/authentication-token\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"entitlement-set\": {\n" +
            "        \"data\": {\n" +
            "          \"id\": \"org-WV6DfwfxxXvLfvfs\",\n" +
            "          \"type\": \"entitlement-sets\"\n" +
            "        },\n" +
            "        \"links\": {\n" +
            "          \"related\": \"/api/v2/organizations/sample/entitlement-set\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"subscription\": {\n" +
            "        \"links\": {\n" +
            "          \"related\": \"/api/v2/organizations/sample/subscription\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"links\": {\n" +
            "      \"self\": \"/api/v2/organizations/sample\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

            String response3 = "{\n" +
            "  \"data\": {\n" +
            "    \"attributes\": {\n" +
            "      \"name\": \"workspace1\",\n" +
            "      \"resource-count\": 0,\n" +
            "      \"updated-at\": \"2017-11-29T19:18:09.976Z\"\n" +
            "    },\n" +
            "    \"id\": \"12412412341234\"\n," +
            "    \"type\": \"workspaces\"\n" +
            "  }\n" +
            "}";

    @GetMapping(produces = "application/vnd.api+json", path = "/organizations/sample/entitlement-set")
    public ResponseEntity<String> terraformJson() {
        return ResponseEntity.ok(response1);
    }


    @GetMapping(produces = "application/vnd.api+json", path = "organizations/sample")
    public ResponseEntity<String> terraformJson2() {
        return ResponseEntity.ok(response2);
    }

    @PostMapping (produces = "application/vnd.api+json", path = "/organizations/sample/workspaces")
    public ResponseEntity<String> terraformJson3() {
        return ResponseEntity.ok(response3);
    }
}
