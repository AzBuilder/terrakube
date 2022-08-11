package org.terrakube.api;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;


class ProviderTests extends ServerApplicationTests {

    @Test
    void searchProviderAsOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS")).when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/provider")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void searchProviderAsNonOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_DEVELOPERS")).when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/provider")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void createProviderAsOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"provider\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"random\",\n" +
                        "      \"description\": \"Provider description\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}   ")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/provider")
                .then()
                .assertThat()
                .body("data.attributes.name", IsEqual.equalTo("random"))
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void createProviderAsNonOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"provider\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"random\",\n" +
                        "      \"description\": \"Provider description\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}   ")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/provider")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }


}
