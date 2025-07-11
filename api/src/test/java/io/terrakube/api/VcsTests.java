package io.terrakube.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.terrakube.api.plugin.vcs.provider.gitlab.GitLabWebhookService;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;



import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.web.reactive.function.client.WebClient;

class VcsTests extends ServerApplicationTests{

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        wireMockServer.resetAll();
    }

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
        String vcsId = given()
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
                .statusCode(HttpStatus.CREATED.value()).extract().path("data.id");

        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .delete("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs/" + vcsId)
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.NO_CONTENT.value());
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

    @Test
    void readVCSAsInternal() {
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
    void readVCSAsAccessToken() {
        //Able to create the vcs connection
        String vcsId = given()
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
                .statusCode(HttpStatus.CREATED.value()).extract().path("data.id");

        //Read the vcs connection as TERRAKUBE_DEVELOPERS does not include the access token
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs/"+vcsId)
                .then()
                .assertThat()
                .body("data.attributes", not(hasKey("accessToken")))
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

        //Read the vcs connection as TERRAKUBE_ADMIN does not include the access token
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_ADMIN"))
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs/"+vcsId)
                .then()
                .assertThat()
                .body("data.attributes", not(hasKey("accessToken")))
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

        //Read the vcs connection as the registry include the access token
        given()
                .headers("Authorization", "Bearer " + generateSystemToken())
                .when()
                .get("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs/"+vcsId)
                .then()
                .assertThat()
                .body("data.attributes", hasKey("accessToken"))
                .body("data.attributes.accessToken", IsEqual.equalTo("12345"))
                .log()
                .all()
                .statusCode(HttpStatus.OK.value());

        // Delete the test vcs connection
        given()
                .headers("Authorization", "Bearer " + generatePAT("TERRAKUBE_DEVELOPERS"))
                .when()
                .delete("/api/v1/organization/d9b58bd3-f3fc-4056-a026-1163297e80a8/vcs/"+vcsId)
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void gitlabGetIdProject() throws IOException, InterruptedException {
        String simpleSearch="[\n" +
                "    {\n" +
                "        \"id\": 5397249,\n" +
                "        \"path_with_namespace\": \"alfespa17/simple-terraform\"\n" +
                "    }\n" +
                "]";

        stubFor(get(urlPathEqualTo("/projects"))
                .withQueryParam("membership", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(simpleSearch)));

        GitLabWebhookService gitLabWebhookService = new GitLabWebhookService(new ObjectMapper(), "localhost", "http://localhost", WebClient.builder());

        Assert.equals("5397249", gitLabWebhookService.getGitlabProjectId("alfespa17/simple-terraform", "12345", "http://localhost:9999"));

        String projectSearch="[\n" +
                "    {\n" +
                "        \"id\": 7138024,\n" +
                "        \"path\": \"simple-terraform\",\n" +
                "        \"path_with_namespace\": \"terraform2745926/simple-terraform\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 7107040,\n" +
                "        \"path_with_namespace\": \"terraform2745926/test/simple-terraform\"\n" +
                "    }\n" +
                "]";
        stubFor(get(urlPathEqualTo("/projects"))
                .withQueryParam("membership", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(projectSearch)));

        Assert.equals("7107040", gitLabWebhookService.getGitlabProjectId("terraform2745926/test/simple-terraform", "12345", "http://localhost:9999"));

        stubFor(get(urlPathEqualTo("/projects"))
                .withQueryParam("membership", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(projectSearch)));

        Assert.equals("7138024", gitLabWebhookService.getGitlabProjectId("terraform2745926/simple-terraform", "12345", "http://localhost:9999"));

    }
}
