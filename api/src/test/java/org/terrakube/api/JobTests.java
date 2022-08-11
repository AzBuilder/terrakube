package org.terrakube.api;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import static io.restassured.RestAssured.given;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class JobTests extends ServerApplicationTests{

    @Test
    void createJobAsOrgMember() {
        mockServer.reset();
        mockServer.when(
                request()
                        .withMethod(HttpMethod.POST.name())
                        .withPath("/api/v1/terraform-rs")
        ).respond(
                response().withStatusCode(HttpStatus.ACCEPTED.value()).withBody("")
        );


        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"job\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"templateReference\": \"2db36f7c-f549-4341-a789-315d47eb061d\"\n" +
                        "    },\n" +
                        "    \"relationships\":{\n" +
                        "        \"workspace\":{\n" +
                        "            \"data\":{\n" +
                        "                \"type\": \"workspace\",\n" +
                        "                \"id\": \"5ed411ca-7ab8-4d2f-b591-02d0d5788afc\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/job")
                .then()
                .assertThat()
                .body("data.attributes.templateReference", IsEqual.equalTo("2db36f7c-f549-4341-a789-315d47eb061d"))
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());
    }

}
