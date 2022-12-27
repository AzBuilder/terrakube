package org.terrakube.api;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static io.restassured.RestAssured.given;

public class LocalStorageTests extends ServerApplicationTests {

    private static final String OUTPUT_DIRECTORY = "%s/.terraform-spring-boot/local/output/%s/%s/%s.tfoutput";
    private static final String STATE_DIRECTORY = "%s/.terraform-spring-boot/local/state/%s/%s/%s/%s/terraformLibrary.tfPlan";
    private static final String STATE_DIRECTORY_JSON = "%s/.terraform-spring-boot/local/state/%s/%s/state/%s.json";

    @Test
    void testLocalStorageJSON() throws IOException {
        FileUtils.writeStringToFile(
                new File(
                        String.format(STATE_DIRECTORY_JSON, FileUtils.getUserDirectoryPath(), "1", "1", "1")),
                "SAMPLE",
                Charset.defaultCharset().toString()
        );

        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/tfstate/v1/organization/1/workspace/1/state/1.json")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

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
