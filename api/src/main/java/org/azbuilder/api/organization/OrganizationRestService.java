package org.azbuilder.api.organization;

import org.azbuilder.api.organization.model.OrganizationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "organization")
@RequestMapping("/api/v1/")
public interface OrganizationRestService {

    @Operation(summary = "Get all organization", description = "Get all organization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationDTO.class))))
    })
    @GetMapping(value = "/organization", produces = { "application/json" })
    ResponseEntity<List<OrganizationDTO>> getAllOrganizations();


    @Operation(
            summary = "Find organization by Id",
            description = "Returns a single organization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the organization",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid organization id supplied", content = @Content),
            @ApiResponse( responseCode = "404", description = "Organization not found", content = @Content)})
    @GetMapping(value = "/organization/{organizationId}", produces = {"application/json"})
    ResponseEntity<OrganizationDTO> getOrganizationById(
            @Parameter(description = "Id of organization to return", required = true)
            @PathVariable("organizationId") String organizationId) ;


    @Operation(summary = "Add a new organization", description = "Add a new organization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrganizationDTO.class))
                    }),
            @ApiResponse(responseCode = "405", description = "Invalid input")
    })
    @PostMapping(value = "/organization", consumes = {"application/json"})
    void addOrganization(
            @Parameter(description = "Create a new organization", required = true)
            @Valid @RequestBody OrganizationDTO organization);

}
