package org.azbuilder.api.workspace;

import org.azbuilder.api.workspace.model.WorkspaceDTO;
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

@Tag(name = "workspace")
@RequestMapping("/api/v1/")
public interface WorkspaceRestService {

    @Operation(summary = "Get all workspaces", description = "Get all workspaces")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = WorkspaceDTO.class))))
    })
    @GetMapping(value = "/organization/{organizationId}/workspace", produces = { "application/json" })
    ResponseEntity<List<WorkspaceDTO>> getAllWorkspaces(@Parameter(description = "Id of organization", required = true)
                                                        @PathVariable("organizationId") String organizationId);


    @Operation(
            summary = "Find workspace by Id",
            description = "Returns a single workspace")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the workspace",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid workspace id supplied", content = @Content),
            @ApiResponse( responseCode = "404", description = "Workspace not found", content = @Content)})
    @GetMapping(value = "/organization/{organizationId}/workspace/{workspaceId}", produces = {"application/json"})
    ResponseEntity<WorkspaceDTO> getWorkspaceById(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Id of workspace to return", required = true)
            @PathVariable("workspaceId") String workspaceId
            ) ;


    @Operation(summary = "Add a new workspace", description = "Add a new workspace")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WorkspaceDTO.class))
                    }),
            @ApiResponse(responseCode = "405", description = "Invalid input")
    })
    @PostMapping(value = "/organization/{organizationId}/workspace", consumes = {"application/json"})
    void addOrganization(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Create a new workspace", required = true)
            @Valid @RequestBody WorkspaceDTO workspaceDTO);

}
