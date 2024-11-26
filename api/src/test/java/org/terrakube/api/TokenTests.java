package org.terrakube.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

class TokenTests extends ServerApplicationTests {

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

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
