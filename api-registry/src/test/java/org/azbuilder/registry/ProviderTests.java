package org.azbuilder.registry;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ProviderTests extends OpenRegistryApplicationTests{

    private static final String PATH_SEARCH="/api/v1/organization";
    private static final String PATH_SEARCH_BODY="{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"type\": \"organization\",\n" +
            "            \"id\": \"b8938f8f-92c7-40fc-90ce-e4725ee8e986\",\n" +
            "            \"attributes\": {\n" +
            "                \"description\": \"Sample Organization\",\n" +
            "                \"name\": \"sampleOrganization\"\n" +
            "            },\n" +
            "            \"relationships\": {\n" +
            "                \"job\": {\n" +
            "                    \"data\": []\n" +
            "                },\n" +
            "                \"module\": {\n" +
            "                    \"data\": []\n" +
            "                },\n" +
            "                \"provider\": {\n" +
            "                    \"data\": [\n" +
            "                        {\n" +
            "                            \"type\": \"provider\",\n" +
            "                            \"id\": \"46c6327b-6580-4204-ad2f-088c03d5251a\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                \"workspace\": {\n" +
            "                    \"data\": []\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private static final String PATH_SEARCH_IMPLEMENTATION="/api/v1/organization/b8938f8f-92c7-40fc-90ce-e4725ee8e986/provider/46c6327b-6580-4204-ad2f-088c03d5251a/version";
    private static final String PATH_SEARCH_IMPLEMENTATION_BODY="{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"type\": \"version\",\n" +
            "            \"id\": \"3bc6b6ac-9aa1-438b-8108-bbed1d61db33\",\n" +
            "            \"attributes\": {\n" +
            "                \"protocols\": \"5.0\",\n" +
            "                \"versionNumber\": \"2.0.0\"\n" +
            "            },\n" +
            "            \"relationships\": {\n" +
            "                \"implementation\": {\n" +
            "                    \"data\": [\n" +
            "                        {\n" +
            "                            \"type\": \"implementation\",\n" +
            "                            \"id\": \"05a71052-cccb-42e6-8754-faf39d37c677\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                \"provider\": {\n" +
            "                    \"data\": {\n" +
            "                        \"type\": \"provider\",\n" +
            "                        \"id\": \"46c6327b-6580-4204-ad2f-088c03d5251a\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"included\": [\n" +
            "        {\n" +
            "            \"type\": \"implementation\",\n" +
            "            \"id\": \"05a71052-cccb-42e6-8754-faf39d37c677\",\n" +
            "            \"attributes\": {\n" +
            "                \"arch\": \"amd64\",\n" +
            "                \"asciiArmor\": \"sampleData\",\n" +
            "                \"downloadUrl\": \"sampleData\",\n" +
            "                \"filename\": \"sampleData.zip\",\n" +
            "                \"keyId\": \"sampleData\",\n" +
            "                \"os\": \"darwin\",\n" +
            "                \"shasum\": \"sampleData\",\n" +
            "                \"shasumsSignatureUrl\": \"sampleData.sig\",\n" +
            "                \"shasumsUrl\": \"sampleData\",\n" +
            "                \"source\": \"sampleData\",\n" +
            "                \"sourceUrl\": \"sampleData\",\n" +
            "                \"trustSignature\": \"\"\n" +
            "            },\n" +
            "            \"relationships\": {\n" +
            "                \"version\": {\n" +
            "                    \"data\": {\n" +
            "                        \"type\": \"version\",\n" +
            "                        \"id\": \"3bc6b6ac-9aa1-438b-8108-bbed1d61db33\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private static final String PATH_SEARCH_IMPLEMENTATION_VERSION_BODY="{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"type\": \"version\",\n" +
            "            \"id\": \"3bc6b6ac-9aa1-438b-8108-bbed1d61db33\",\n" +
            "            \"attributes\": {\n" +
            "                \"protocols\": \"5.0\",\n" +
            "                \"versionNumber\": \"2.0.0\"\n" +
            "            },\n" +
            "            \"relationships\": {\n" +
            "                \"implementation\": {\n" +
            "                    \"data\": [\n" +
            "                        {\n" +
            "                            \"type\": \"implementation\",\n" +
            "                            \"id\": \"05a71052-cccb-42e6-8754-faf39d37c677\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                \"provider\": {\n" +
            "                    \"data\": {\n" +
            "                        \"type\": \"provider\",\n" +
            "                        \"id\": \"46c6327b-6580-4204-ad2f-088c03d5251a\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    ]" +
            "}";
    private static final String PATH_SEARCH_IMPLEMENTATION_FILE="/api/v1/organization/b8938f8f-92c7-40fc-90ce-e4725ee8e986/provider/46c6327b-6580-4204-ad2f-088c03d5251a/version/3bc6b6ac-9aa1-438b-8108-bbed1d61db33/implementation";
    private static final String PATH_SEARCH_IMPLEMENTATION_FILE_BODY="{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"type\": \"implementation\",\n" +
            "            \"id\": \"05a71052-cccb-42e6-8754-faf39d37c677\",\n" +
            "            \"attributes\": {\n" +
            "                \"arch\": \"amd64\",\n" +
            "                \"asciiArmor\": \"sampleData\",\n" +
            "                \"downloadUrl\": \"sampleData\",\n" +
            "                \"filename\": \"sampleData.zip\",\n" +
            "                \"keyId\": \"sampleData\",\n" +
            "                \"os\": \"darwin\",\n" +
            "                \"shasum\": \"sampleData\",\n" +
            "                \"shasumsSignatureUrl\": \"sampleData.sig\",\n" +
            "                \"shasumsUrl\": \"sampleData\",\n" +
            "                \"source\": \"sampleData\",\n" +
            "                \"sourceUrl\": \"sampleData\",\n" +
            "                \"trustSignature\": \"\"\n" +
            "            },\n" +
            "            \"relationships\": {\n" +
            "                \"version\": {\n" +
            "                    \"data\": {\n" +
            "                        \"type\": \"version\",\n" +
            "                        \"id\": \"3bc6b6ac-9aa1-438b-8108-bbed1d61db33\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";


    @Test
    void providerApiGetTestStep1() {
       mockServer.when(
               request()
                       .withMethod(HttpMethod.GET.name())
                       .withPath(PATH_SEARCH)
                       .withQueryStringParameter("filter[organization]","name==sampleOrganization")
                       .withQueryStringParameter("filter[provider]","name==sampleProvider")
       ).respond(
         response().withStatusCode(HttpStatus.SC_OK).withBody(PATH_SEARCH_BODY)
       );


        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath(PATH_SEARCH_IMPLEMENTATION)
                        .withQueryStringParameter("include","implementation")
        ).respond(
                response().withStatusCode(HttpStatus.SC_OK).withBody(PATH_SEARCH_IMPLEMENTATION_BODY)
        );

        when()
                .get("/terraform/providers/v1/sampleOrganization/sampleProvider/versions")
                .then()
                .log().all()
                .body("versions",hasSize(1))
                .body("versions[0].version",equalTo("2.0.0"))
                .body("versions[0].protocols",hasSize(1))
                .body("versions[0].protocols[0]",equalTo("5.0"))
                .body("versions[0].platforms[0].os",equalTo("darwin"))
                .body("versions[0].platforms[0].arch",equalTo("amd64"))
                .log().all()
                .statusCode(HttpStatus.SC_OK);

    }

    @Test
    void providerApiGetTestStep2() {
        mockServer.reset();
        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath(PATH_SEARCH)
                        .withQueryStringParameter("filter[organization]","name==sampleOrganization")
                        .withQueryStringParameter("filter[provider]","name==sampleProvider")
        ).respond(
                response().withStatusCode(HttpStatus.SC_OK).withBody(PATH_SEARCH_BODY)
        );

        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath(PATH_SEARCH_IMPLEMENTATION)
                        .withQueryStringParameter("include","implementation")
        ).respond(
                response().withStatusCode(HttpStatus.SC_OK).withBody(PATH_SEARCH_IMPLEMENTATION_BODY)
        );

        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath(PATH_SEARCH_IMPLEMENTATION)
                        .withQueryStringParameter("filter[version]","versionNumber==2.0.0")
        ).respond(
                response().withStatusCode(HttpStatus.SC_OK).withBody(PATH_SEARCH_IMPLEMENTATION_VERSION_BODY)
        );

        mockServer.when(
                request()
                        .withMethod(HttpMethod.GET.name())
                        .withPath(PATH_SEARCH_IMPLEMENTATION_FILE)
                        .withQueryStringParameter("filter[implementation]","os==darwin")
                        .withQueryStringParameter("arch","=amd64")
        ).respond(
                response().withStatusCode(HttpStatus.SC_OK).withBody(PATH_SEARCH_IMPLEMENTATION_FILE_BODY)
        );

        when()
                .get("/terraform/providers/v1/sampleOrganization/sampleProvider/versions")
                .then()
                .log().all()
                .body("versions",hasSize(1))
                .body("versions[0].version",equalTo("2.0.0"))
                .body("versions[0].protocols",hasSize(1))
                .body("versions[0].protocols[0]",equalTo("5.0"))
                .body("versions[0].platforms[0].os",equalTo("darwin"))
                .body("versions[0].platforms[0].arch",equalTo("amd64"))
                .log().all()
                .statusCode(HttpStatus.SC_OK);

        when()
                .get("/terraform/providers/v1/sampleOrganization/sampleProvider/2.0.0/download/darwin/amd64")
                .then()
                .log().all()
                .body("protocols",hasSize(1))
                .body("protocols[0]",equalTo("5.0"))
                .body("os",equalTo("darwin"))
                .body("arch",equalTo("amd64"))
                .body("filename",equalTo("sampleData.zip"))
                .body("download_url",equalTo("sampleData"))
                .body("shasums_url",equalTo("sampleData"))
                .body("shasums_signature_url",equalTo("sampleData.sig"))
                .body("shasum",equalTo("sampleData"))
                .body("signing_keys.gpg_public_keys[0].key_id",equalTo("sampleData"))
                .body("signing_keys.gpg_public_keys[0].ascii_armor",equalTo("sampleData"))
                .body("signing_keys.gpg_public_keys[0].trust_signature",equalTo(""))
                .body("signing_keys.gpg_public_keys[0].source",equalTo("sampleData"))
                .body("signing_keys.gpg_public_keys[0].source_url",equalTo("sampleData"))
                .log().all()
                .statusCode(HttpStatus.SC_OK);

    }
}
