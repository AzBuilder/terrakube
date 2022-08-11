package org.terrakube.api;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class SshTests extends ServerApplicationTests {

    @Test
    void searchSshAsOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/ssh")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void createSshAsOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"ssh\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"SSH RANDOM DATA\",\n" +
                        "      \"description\": \"SSH KEY DESCRIPTION\",\n" +
                        "      \"privateKey\": \"12345\", \n" +
                        "      \"sshType\": \"rsa\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/f5365c9e-bc11-4781-b649-45a281ccdd4a/ssh")
                .then()
                .assertThat()
                .body("data.attributes.name", IsEqual.equalTo("SSH RANDOM DATA"))
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void searchSshAsNonOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_DEVELOPERS"))
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/ssh")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void createSshAsNonOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"ssh\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"SSH KEY NAME\",\n" +
                        "      \"description\": \"SSH KEY DESCRIPTION\",\n" +
                        "      \"privateKey\": \"{{sshPrivateKey}}\", \n" +
                        "      \"sshType\": \"{{sshKeyType}}\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/f5365c9e-bc11-4781-b649-45a281ccdd4a/ssh")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
