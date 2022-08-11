package org.terrakube.api;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

class VcsTests extends ServerApplicationTests{

    @Test
    void searchVcsAsOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());
    }
    @Test
    void searchVcsAsNonOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_DEVELOPERS"))
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void createVcsAsOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"vcs\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"githubConnection\",\n" +
                        "      \"description\": \"vcsGitHubDescription\",\n" +
                        "      \"vcsType\": \"GITHUB\",\n" +
                        "      \"clientId\": \"12345\",\n" +
                        "      \"clientSecret\": \"12345\",\n" +
                        "      \"accessToken\": \"12345\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs")
                .then()
                .assertThat()
                .body("data.attributes.name", IsEqual.equalTo("githubConnection"))
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }
    @Test
    void createVcsAsNonOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"vcs\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"githubConnection\",\n" +
                        "      \"description\": \"vcsGitHubDescription\",\n" +
                        "      \"vcsType\": \"GITHUB\",\n" +
                        "      \"clientId\": \"12345\",\n" +
                        "      \"clientSecret\": \"12345\",\n" +
                        "      \"accessToken\": \"12345\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
