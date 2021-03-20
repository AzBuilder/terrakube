package org.azbuilder.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                title = "AzTerraBot API",
                version = "1.0.0",
                description = "AzTerraBot API",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
                contact =@Contact(email="me.myAPI@test.com")
        ),
        tags = {
                @Tag(name = "organization", description = "Organization API"),
                @Tag(name = "workspace", description = "Workspace API"),
                @Tag(name = "variable", description = "Variable API"),
                @Tag(name = "module", description = "Module API")
        },
        servers = { @Server(
                description = "PROD basePath",
                url = "https://{test}.api.com/base/path/{version}/",
                variables = {
                        @ServerVariable(name="client", defaultValue = "prod"),
                        @ServerVariable(name="version", defaultValue = "v1")}),
                @Server(
                        description = "UAT basePath",
                        url = "https://{test}.api.com/base/path/{version}/",
                        variables = {
                                @ServerVariable(name="client", defaultValue = "uat"),
                                @ServerVariable(name="version", defaultValue = "v1")})        },
        security = {
                @SecurityRequirement(name = "basicAuth"),
                @SecurityRequirement(name = "APIKey")}
)
public class OpenApiConfiguration {
}
