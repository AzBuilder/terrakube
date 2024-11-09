package org.terrakube.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.terrakube.api.repository.TeamRepository;
import org.terrakube.api.rs.team.Team;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class CollectionTests extends ServerApplicationTests {

    @Autowired
    TeamRepository teamRepository;

    @Test
    void createCollectionAsOrgMember() {

        Optional<Team> teamOptional = teamRepository.findById(UUID.fromString("58529721-425e-44d7-8b0d-1d515043c2f7"));
        Team team = teamOptional.get();
        team.setManageCollection(true);
        teamRepository.save(team);

        String collectionId = given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"collection\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"Collection1\",\n" +
                        "      \"description\": \"Sample Description\",\n" +
                        "      \"priority\": 10\n" +
                        "      }" +
                        "   }" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value()).extract().path("data.id");


        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"item\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"key\": \"random_key\",\n" +
                        "      \"value\": \"random_key\",\n" +
                        "      \"sensitive\": true,\n" +
                        "      \"hcl\": false,\n" +
                        "      \"category\": \"ENV\",\n" +
                        "      \"description\": \"random_description\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/"+collectionId+"/item/")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"collection\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"Collection1\",\n" +
                        "      \"description\": \"Sample Description\",\n" +
                        "      \"priority\": 10\n" +
                        "      }" +
                        "   }" +
                        "}")
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/"+collectionId+"/item/")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

        String referenceId = given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"reference\",\n" +
                        "    \"attributes\": {\n" +
                        "            \"description\": \"my random description\"\n" +
                        "         },\n" +
                        "    \"relationships\": {\n" +
                        "      \"workspace\": {\n" +
                        "        \"data\": {\n" +
                        "          \"type\": \"workspace\",\n" +
                        "          \"id\": \"5ed411ca-7ab8-4d2f-b591-02d0d5788afc\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/"+collectionId+"/reference/")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value()).extract().path("data.id");


        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"collection\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"Collection1\",\n" +
                        "      \"description\": \"Sample Description\",\n" +
                        "      \"priority\": 10\n" +
                        "      }" +
                        "   }" +
                        "}")
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/"+collectionId+"/reference/"+referenceId)
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

        team.setManageCollection(false);
        teamRepository.save(team);
    }

    @Test
    void createCollectionAsNonOrgMember() {
        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_GROUP"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"collection\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"Collection1\",\n" +
                        "      \"description\": \"Sample Description\",\n" +
                        "      \"priority\": 10\n" +
                        "      }" +
                        "   }" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());

        Optional<Team> teamOptional = teamRepository.findById(UUID.fromString("58529721-425e-44d7-8b0d-1d515043c2f7"));
        Team team = teamOptional.get();
        team.setManageCollection(true);
        teamRepository.save(team);

        String collectionId = given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"collection\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"name\": \"collecton_random\",\n" +
                        "      \"description\": \"Sample Description\",\n" +
                        "      \"priority\": 10\n" +
                        "      }" +
                        "   }" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value()).extract().path("data.id");


        given()
                .headers("Authorization", "Bearer " + generatePAT("FAKE_GROUP"), "Content-Type", "application/vnd.api+json")
                .body("{\n" +
                        "  \"data\": {\n" +
                        "    \"type\": \"item\",\n" +
                        "    \"attributes\": {\n" +
                        "      \"key\": \"random_key\",\n" +
                        "      \"value\": \"random_key\",\n" +
                        "      \"sensitive\": true,\n" +
                        "      \"hcl\": false,\n" +
                        "      \"category\": \"ENV\",\n" +
                        "      \"description\": \"random_description\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/collection/"+collectionId+"/item/")
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());

        team.setManageCollection(false);
        teamRepository.save(team);

    }


}
