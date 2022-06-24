package org.terrakube.api;

import com.yahoo.elide.core.exceptions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.yahoo.elide.test.jsonapi.JsonApiDSL.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class ProviderTests extends ServerApplicationTests{

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE variable; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO provider (id, name, description, organization_id) VALUES\n" +
                    "\t\t('b5e41ba0-e7a5-4643-9200-1c45c5b82648','Provider','Description','a42f538b-8c75-4311-8e73-ea2c0f2fb577');"
    })
    void providerApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/provider")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "provider"),
                                        id("b5e41ba0-e7a5-4643-9200-1c45c5b82648"),
                                        attributes(
                                                attr("description", "Description"),
                                                attr("name", "Provider")
                                        ),
                                        relationships(
                                                relation("organization",true,
                                                        resource(
                                                                type("organization"),
                                                                id("a42f538b-8c75-4311-8e73-ea2c0f2fb577")
                                                        )
                                                ),
                                                relation("version")
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Sql(statements = {
            "DELETE SCHEDULE; DELETE step; DELETE  history; DELETE job; DELETE secret; DELETE variable; DELETE environment; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO provider (id, name, description, organization_id) VALUES\n" +
                    "\t\t('b5e41ba0-e7a5-4643-9200-1c45c5b82648','Provider','Description','a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO version (id, version_number, protocols, provider_id) VALUES\n" +
                    "\t\t('c4d8f2c0-0f5b-4a9d-921e-71cd082a566b','1.0.0','5.0','b5e41ba0-e7a5-4643-9200-1c45c5b82648');"
    })
    void versionApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/provider/b5e41ba0-e7a5-4643-9200-1c45c5b82648/version")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "version"),
                                        id("c4d8f2c0-0f5b-4a9d-921e-71cd082a566b"),
                                        attributes(
                                                attr("protocols", "5.0"),
                                                attr("versionNumber", "1.0.0")
                                        ),
                                        relationships(
                                                relation("implementation"),
                                                relation("provider",true,
                                                        resource(
                                                                type("provider"),
                                                                id("b5e41ba0-e7a5-4643-9200-1c45c5b82648")
                                                        )
                                                )
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Sql(statements = {
            "DELETE step; DELETE  history; DELETE job; DELETE secret; DELETE variable; DELETE environment; DELETE workspace; DELETE implementation; DELETE version; DELETE module; DELETE vcs; DELETE FROM provider; DELETE FROM team; DELETE FROM organization;",
            "INSERT INTO organization (id, name, description) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb577','Organization','Description');",
            "INSERT INTO team (id, name, manage_workspace, manage_module, manage_provider, organization_id) VALUES\n" +
                    "\t\t('a42f538b-8c75-4311-8e73-ea2c0f2fb579','sample_team', true, true, true, 'a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO provider (id, name, description, organization_id) VALUES\n" +
                    "\t\t('b5e41ba0-e7a5-4643-9200-1c45c5b82648','Provider','Description','a42f538b-8c75-4311-8e73-ea2c0f2fb577');",
            "INSERT INTO version (id, version_number, protocols, provider_id) VALUES\n" +
                    "\t\t('c4d8f2c0-0f5b-4a9d-921e-71cd082a566b','1.0.0','5.0','b5e41ba0-e7a5-4643-9200-1c45c5b82648');",
            "INSERT INTO implementation (id, os, arch, filename, download_url, shasums_url, shasums_signature_url, shasum, key_id, ascii_armor, trust_signature, source, source_url, version_id) VALUES\n" +
                    "\t\t('8f7b0ea3-8018-4b22-9513-9cf848ce4292','osData','archData','file.txt', 'download.com','shasumsUrlData','shasumsSignatureUrlData','shasumData','keyData','asciiData','trustSignatureData','sourceData','sourceData.com','c4d8f2c0-0f5b-4a9d-921e-71cd082a566b');"
    })
    void implementationApiGetTest() {
        when()
                .get("/api/v1/organization/a42f538b-8c75-4311-8e73-ea2c0f2fb577/provider/b5e41ba0-e7a5-4643-9200-1c45c5b82648/version/c4d8f2c0-0f5b-4a9d-921e-71cd082a566b/implementation")
                .then()
                .log().all()
                .body(equalTo(
                        data(
                                resource(
                                        type( "implementation"),
                                        id("8f7b0ea3-8018-4b22-9513-9cf848ce4292"),
                                        attributes(
                                                attr("arch", "archData"),
                                                attr("asciiArmor", "asciiData"),
                                                attr("downloadUrl", "download.com"),
                                                attr("filename", "file.txt"),
                                                attr("keyId", "keyData"),
                                                attr("os", "osData"),
                                                attr("shasum", "shasumData"),
                                                attr("shasumsSignatureUrl", "shasumsSignatureUrlData"),
                                                attr("shasumsUrl", "shasumsUrlData"),
                                                attr("source", "sourceData"),
                                                attr("sourceUrl", "sourceData.com"),
                                                attr("trustSignature", "trustSignatureData")
                                        ),
                                        relationships(
                                                relation("version",true,
                                                        resource(
                                                                type("version"),
                                                                id("c4d8f2c0-0f5b-4a9d-921e-71cd082a566b")
                                                        )
                                                )
                                        )
                                )
                        ).toJSON())
                )
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }
}
