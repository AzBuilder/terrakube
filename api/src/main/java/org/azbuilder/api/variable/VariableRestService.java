package org.azbuilder.api.variable;

import org.azbuilder.api.variable.model.VariableDTO;
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

@Tag(name = "variable")
@RequestMapping("/api/v1/")
public interface VariableRestService {


    @Operation(summary = "Get all variables", description = "Get all variables")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VariableDTO.class))))
    })
    @GetMapping(value = "/organization/{organizationId}/workspace/{workspaceId}/variable", produces = { "application/json" })
    ResponseEntity<List<VariableDTO>> getAllVariables(@Parameter(description = "Id of organization", required = true)
                                                        @PathVariable("organizationId") String organizationId,
                                                        @Parameter(description = "Id of workspace", required = true)
                                                        @PathVariable("workspaceId") String workspaceId);


    @Operation(
            summary = "Find variable by Id",
            description = "Returns a single variable")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the variable",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = VariableDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid variable id supplied", content = @Content),
            @ApiResponse( responseCode = "404", description = "Variable not found", content = @Content)})
    @GetMapping(value = "/organization/{organizationId}/workspace/{workspaceId}/variable/{variableId}", produces = {"application/json"})
    ResponseEntity<VariableDTO> getVariableById(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Id of workspace to return", required = true)
            @PathVariable("workspaceId") String workspaceId,
            @Parameter(description = "Id of variable to return", required = true)
            @PathVariable("variableId") String variableId
    ) ;


    @Operation(summary = "Add a new variable", description = "Add a new variable")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VariableDTO.class))
                    }),
            @ApiResponse(responseCode = "405", description = "Invalid input")
    })
    @PostMapping(value = "/organization/{organizationId}/workspace/{workspaceId}/variable", consumes = {"application/json"})
    void addVariable(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String workspaceId,
            @Parameter(description = "Create a new variable", required = true)
            @Valid @RequestBody VariableDTO variableDTO);

}
