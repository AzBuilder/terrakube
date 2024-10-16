package org.terrakube.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

class TokenTests extends ServerApplicationTests {

    @Test
    void createToken() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .headers("Content-Type", "application/vnd.api+json")
                .when()
                .body("{\"description\":\"12345\",\"days\":1}")
                .post("/pat/v1")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }

}
