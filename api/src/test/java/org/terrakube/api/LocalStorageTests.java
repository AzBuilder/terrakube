package org.terrakube.api;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.terrakube.api.repository.TeamRepository;
import org.terrakube.api.rs.team.Team;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class LocalStorageTests extends ServerApplicationTests {

    private static final String OUTPUT_DIRECTORY = "%s/.terraform-spring-boot/local/output/%s/%s/%s.tfoutput";
    private static final String STATE_DIRECTORY = "%s/.terraform-spring-boot/local/state/%s/%s/%s/%s/terraformLibrary.tfPlan";
    private static final String STATE_DIRECTORY_JSON = "%s/.terraform-spring-boot/local/state/%s/%s/state/%s.json";

    @Autowired
    TeamRepository teamRepository;

    @Test
    void testLocalStorageJSON() throws IOException {
        FileUtils.writeStringToFile(
                new File(
                        String.format(STATE_DIRECTORY_JSON, FileUtils.getUserDirectoryPath(), "d9b58bd3-f3fc-4056-a026-1163297e80a8", "5ed411ca-7ab8-4d2f-b591-02d0d5788afc", "1")),
                "SAMPLE",
                Charset.defaultCharset().toString()
        );

        Optional<Team> teamOptional = teamRepository.findById(UUID.fromString("58529721-425e-44d7-8b0d-1d515043c2f7"));
        Team team = teamOptional.get();
        team.setManageState(true);
        teamRepository.save(team);

        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/tfstate/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/workspace/5ed411ca-7ab8-4d2f-b591-02d0d5788afc/state/1.json")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

    }

    @Test
    void testLocalStorageJSONWithoutManageStatePermission() throws IOException {
        FileUtils.writeStringToFile(
                new File(
                        String.format(STATE_DIRECTORY_JSON, FileUtils.getUserDirectoryPath(), "d9b58bd3-f3fc-4056-a026-1163297e80a8", "5ed411ca-7ab8-4d2f-b591-02d0d5788afc", "1")),
                "SAMPLE",
                Charset.defaultCharset().toString()
        );

        Optional<Team> teamOptional = teamRepository.findById(UUID.fromString("58529721-425e-44d7-8b0d-1d515043c2f7"));
        Team team = teamOptional.get();
        team.setManageState(false);
        teamRepository.save(team);

        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/tfstate/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/workspace/5ed411ca-7ab8-4d2f-b591-02d0d5788afc/state/1.json")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.FORBIDDEN.value());

    }

    @Test
    void testLocalStorageBinaryState() throws IOException {
        FileUtils.writeStringToFile(
                new File(
                        String.format(STATE_DIRECTORY, FileUtils.getUserDirectoryPath(), "2", "2", "2", "2")),
                "SAMPLE",
                Charset.defaultCharset().toString()
        );

        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/tfstate/v1/organization/2/workspace/2/jobId/2/step/2/terraform.tfstate")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

    }

    @Test
    void testLocalStorageOutputJob() throws IOException {
        FileUtils.writeStringToFile(
                new File(
                        String.format(OUTPUT_DIRECTORY, FileUtils.getUserDirectoryPath(), "3", "3", "3")),
                "SAMPLE",
                Charset.defaultCharset().toString()
        );

        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/tfoutput/v1/organization/3/job/3/step/3")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

    }

}
