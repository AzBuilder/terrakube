package org.azbuilder.api.module;

import org.azbuilder.api.module.model.ModuleDTO;
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

@Tag(name = "module")
@RequestMapping("/api/v1/")
public interface ModuleRestService {


    @Operation(summary = "Get all modules", description = "Get all modules")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ModuleDTO.class))))
    })
    @GetMapping(value = "/organization/{organizationId}/module", produces = { "application/json" })
    ResponseEntity<List<ModuleDTO>> getAllModules(@Parameter(description = "Id of the organization", required = true)
                                                        @PathVariable("organizationId") String organizationId);


    @Operation(
            summary = "Find module by Id",
            description = "Returns a single module")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the module",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ModuleDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid module id supplied", content = @Content),
            @ApiResponse( responseCode = "404", description = "Module not found", content = @Content)})
    @GetMapping(value = "/organization/{organizationId}/module/{moduleId}", produces = {"application/json"})
    ResponseEntity<ModuleDTO> getModuleById(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Id of module to return", required = true)
            @PathVariable("moduleId") String moduleId
    ) ;


    @Operation(summary = "Add a new module", description = "Add a new module")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ModuleDTO.class))
                    }),
            @ApiResponse(responseCode = "405", description = "Invalid input")
    })
    @PostMapping(value = "/organization/{organizationId}/module", consumes = {"application/json"})
    void addModule(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Create a new module", required = true)
            @Valid @RequestBody ModuleDTO moduleDTO);

}
