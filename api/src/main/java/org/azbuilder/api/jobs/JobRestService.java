package org.azbuilder.api.jobs;

import org.azbuilder.api.jobs.model.JobDTO;
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

@Tag(name = "job")
@RequestMapping("/api/v1/")
public interface JobRestService {


    @Operation(summary = "Get all jobs", description = "Get all jobs")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JobDTO.class))))
    })
    @GetMapping(value = "/organization/{organizationId}/workspace/{workspaceId}/job", produces = { "application/json" })
    ResponseEntity<List<JobDTO>> getAllJobs(@Parameter(description = "Id of organization", required = true)
                                                      @PathVariable("organizationId") String organizationId,
                                                      @Parameter(description = "Id of workspace", required = true)
                                                      @PathVariable("workspaceId") String workspaceId);


    @Operation(
            summary = "Find job by Id",
            description = "Returns a single job")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the job",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = JobDTO.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid job id supplied", content = @Content),
            @ApiResponse( responseCode = "404", description = "Job not found", content = @Content)})
    @GetMapping(value = "/organization/{organizationId}/workspace/{workspaceId}/job/{jobId}", produces = {"application/json"})
    ResponseEntity<JobDTO> getJobById(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Id of workspace to return", required = true)
            @PathVariable("workspaceId") String workspaceId,
            @Parameter(description = "Id of job to return", required = true)
            @PathVariable("jobId") String jobId
    ) ;


    @Operation(summary = "Add a new job", description = "Add a new job")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JobDTO.class))
                    }),
            @ApiResponse(responseCode = "405", description = "Invalid input")
    })
    @PostMapping(value = "/organization/{organizationId}/workspace/{workspaceId}/job", consumes = {"application/json"})
    void addJob(
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String organizationId,
            @Parameter(description = "Id of organization", required = true)
            @PathVariable("organizationId") String workspaceId,
            @Parameter(description = "Create a new job", required = true)
            @Valid @RequestBody JobDTO jobDTO);

}
