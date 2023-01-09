package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.terrakube.api.plugin.state.model.configuration.ConfigurationData;
import org.terrakube.api.plugin.state.model.runs.RunsData;
import org.terrakube.api.plugin.state.model.state.StateData;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceData;
import org.springframework.http.HttpEntity;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.terrakube.api.repository.ContentRepository;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/remote/tfe/v2/")
@AllArgsConstructor
public class RemoteTfeController {
    private final ContentRepository contentRepository;

    RemoteTfeService remoteTfeService;

    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/entitlement-set")
    public ResponseEntity<?> getOrgEntitlementSet(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgEntitlementSet(organizationName)));
        /* 
        return ResponseEntity.ok("{\n" +
        "  \"data\": {\n" +
        "    \"id\": \"org-simple\",\n" +
        "    \"type\": \"entitlement-sets\",\n" +
        "    \"attributes\": {\n" +
        "    }\n" +
        "  }\n" +
        "}");*/
        
    }

    @GetMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}")
    public ResponseEntity<?> getOrgInformation(@PathVariable("organizationName") String organizationName) {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getOrgInformation(organizationName)));
        /*
        return ResponseEntity.ok("{\n" +
        "  \"data\": {\n" +
        "    \"id\": \"simple\",\n" +
        "    \"type\": \"organizations\",\n" +
        "    \"attributes\": {\n" +
        "      \"name\": \"simple\"\n" +
        "    },\n" +
        "  }\n" +
        "}");*/
    }

    @GetMapping (produces = "application/vnd.api+json", path = "organizations/{organizationName}/workspaces/{workspaceName}")
    public ResponseEntity<?> getWorkspace(@PathVariable("organizationName") String organizationName, @PathVariable("workspaceName") String workspaceName) {
        log.info("Searching: {} {}", organizationName, workspaceName);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getWorkspace(organizationName, workspaceName, new HashMap())));
    }

    
    @PostMapping(produces = "application/vnd.api+json", path = "organizations/{organizationName}/workspaces")
    public ResponseEntity<?> createWorkspace(@PathVariable("organizationName") String organizationName, @RequestBody WorkspaceData workspaceData) {
        log.info("Create {}", workspaceData.toString());
        // CLI is sending {"data":{"type":"workspaces","attributes":{"name":"workspace1","terraform-version":"1.1.3"}}}
        Optional<WorkspaceData> newWorkspace = Optional.ofNullable(remoteTfeService.createWorkspace(organizationName, workspaceData));
        if(newWorkspace.isPresent()){
            log.info("Created: {}", newWorkspace.get().toString());
            return ResponseEntity.status(201).body(newWorkspace.get());
        }else{
            return ResponseEntity.status(500).body("");
        }
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/actions/lock")
    public ResponseEntity<?> lockWorkspace(@PathVariable("workspaceId") String workspaceId) {
        log.info("Lock {}", workspaceId);
        // CLI is sending {"data":{"type":"workspaces","attributes":{"name":"workspace1","terraform-version":"1.1.3"}}}
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.updateWorkspaceLock(workspaceId, true)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/actions/unlock")
    public ResponseEntity<?> unlockWorkspace(@PathVariable("workspaceId") String workspaceId) {
        log.info("Unlock {}", workspaceId);
        // CLI is sending {"data":{"type":"workspaces","attributes":{"name":"workspace1","terraform-version":"1.1.3"}}}
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.updateWorkspaceLock(workspaceId, false)));
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/state-versions")
    public ResponseEntity<?> createWorkspaceState(@PathVariable("workspaceId") String workspaceId, @RequestBody String StateData) {
        log.info("Create State /remote/tfe/v2/ {}", workspaceId);
        log.info("Body: {}", StateData);
        
        //Optional<StateData> stateData = Optional.ofNullable(remoteTfeService.createWorkspaceState(workspaceId, StateData));
        
        // CLI is sending 
        /*
         * {
   "data":{
      "type":"state-versions",
      "attributes":{
         "force":false,
         "lineage":"fd7f59e4-fb57-e5f7-736c-0588d2acf1c6",
         "md5":"78e0fc083882c81e406b59844649befd",
         "serial":0,
         "state":"ewogICJ2ZXJzaW9uIjogNCwKICAidGVycmFmb3JtX3ZlcnNpb24iOiAiMS4xLjMiLAogICJzZXJpYWwiOiAwLAogICJsaW5lYWdlIjogImZkN2Y1OWU0LWZiNTctZTVmNy03MzZjLTA1ODhkMmFjZjFjNiIsCiAgIm91dHB1dHMiOiB7fSwKICAicmVzb3VyY2VzIjogWwogICAgewogICAgICAibW9kZSI6ICJtYW5hZ2VkIiwKICAgICAgInR5cGUiOiAicmFuZG9tX3N0cmluZyIsCiAgICAgICJuYW1lIjogInJhbmRvbSIsCiAgICAgICJwcm92aWRlciI6ICJwcm92aWRlcltcInJlZ2lzdHJ5LnRlcnJhZm9ybS5pby9oYXNoaWNvcnAvcmFuZG9tXCJdIiwKICAgICAgImluc3RhbmNlcyI6IFsKICAgICAgICB7CiAgICAgICAgICAic2NoZW1hX3ZlcnNpb24iOiAyLAogICAgICAgICAgImF0dHJpYnV0ZXMiOiB7CiAgICAgICAgICAgICJpZCI6ICI1S1JHVlduSFJoMW5KdlY4IiwKICAgICAgICAgICAgImtlZXBlcnMiOiBudWxsLAogICAgICAgICAgICAibGVuZ3RoIjogMTYsCiAgICAgICAgICAgICJsb3dlciI6IHRydWUsCiAgICAgICAgICAgICJtaW5fbG93ZXIiOiAwLAogICAgICAgICAgICAibWluX251bWVyaWMiOiAwLAogICAgICAgICAgICAibWluX3NwZWNpYWwiOiAwLAogICAgICAgICAgICAibWluX3VwcGVyIjogMCwKICAgICAgICAgICAgIm51bWJlciI6IHRydWUsCiAgICAgICAgICAgICJudW1lcmljIjogdHJ1ZSwKICAgICAgICAgICAgIm92ZXJyaWRlX3NwZWNpYWwiOiAiL0DCoyQiLAogICAgICAgICAgICAicmVzdWx0IjogIjVLUkdWV25IUmgxbkp2VjgiLAogICAgICAgICAgICAic3BlY2lhbCI6IHRydWUsCiAgICAgICAgICAgICJ1cHBlciI6IHRydWUKICAgICAgICAgIH0sCiAgICAgICAgICAic2Vuc2l0aXZlX2F0dHJpYnV0ZXMiOiBbXQogICAgICAgIH0KICAgICAgXQogICAgfQogIF0KfQo="
      }
   }
}
         */

         //if(stateData.isPresent()){
         //   return ResponseEntity.status(201).body(stateData.get());
         //}else{
         //   return ResponseEntity.internalServerError().body("");
        // }
        /* 
         return ResponseEntity.status(201).body(        "{\n" +
        "    \"data\": {\n" +
        "        \"id\": \"sv-DmoXecHePnNznaA4\",\n" +
        "        \"type\": \"state-versions\",\n" +
        "        \"attributes\": {\n" +
        "            \"vcs-commit-sha\": null,\n" +
        "            \"vcs-commit-url\": null,\n" +
        "            \"created-at\": \"2018-07-12T20:32:01.490Z\",\n" +
        "            \"hosted-state-download-url\": \"https://archivist.terraform.io/v1/object/f55b739b-ff03-4716-b436-726466b96dc4\",\n" +
        "            \"hosted-json-state-download-url\": \"https://archivist.terraform.io/v1/object/4fde7951-93c0-4414-9a40-f3abc4bac490\",\n" +
        "            \"serial\": 1\n" +
        "        },\n" +
        "        \"links\": {\n" +
        "            \"self\": \"/api/v2/state-versions/sv-DmoXecHePnNznaA4\"\n" +
        "        }\n" +
        "    }\n" +
        "}");*/

        return ResponseEntity.ok("{\n" +
        "    \"data\": {\n" +
        "        \"id\": \"sv-DmoXecHePnNznaA4\",\n" +
        "        \"type\": \"state-versions\",\n" +
        "        \"attributes\": {\n" +
        "            \"vcs-commit-sha\": null,\n" +
        "            \"vcs-commit-url\": null,\n" +
        "            \"created-at\": \"2018-07-12T20:32:01.490Z\",\n" +
        "            \"hosted-state-download-url\": \"https://archivist.terraform.io/v1/object/f55b739b-ff03-4716-b436-726466b96dc4\",\n" +
        "            \"hosted-json-state-download-url\": \"https://archivist.terraform.io/v1/object/4fde7951-93c0-4414-9a40-f3abc4bac490\",\n" +
        "            \"serial\": 1\n" +
        "        },\n" +
        "        \"links\": {\n" +
        "            \"self\": \"/api/v2/state-versions/sv-DmoXecHePnNznaA4\"\n" +
        "        }\n" +
        "    }\n" +
        "}");
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/workspaces/{workspaceId}/configuration-versions")
    public ResponseEntity<?> createConfigurationVersion(@PathVariable("workspaceId") String workspaceId, @RequestBody ConfigurationData configurationData) {
        log.info("Creating Configuration Version for worspaceId {}", workspaceId);
        return ResponseEntity.status(201).body(remoteTfeService.createConfigurationVersion(workspaceId, configurationData));
    }

    @Transactional
    @GetMapping(produces = "application/vnd.api+json", path = "/configuration-versions/{configurationId}")
    public ResponseEntity<?> getConfigurationVersion(@PathVariable("configurationId") String configurationId) {
        log.info("Searching Configuration Version Id {}", configurationId);
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.searchConfiguration(configurationId)));
    }

    @Transactional
    @PutMapping (path = "/configuration-versions/{configurationid}")
    public ResponseEntity<?> uploadConfiguration(HttpServletRequest httpServletRequest, @PathVariable("configurationid") String configurationId) throws IOException {
        log.info("Uploading Id {} file", configurationId );
        remoteTfeService.uploadFile(configurationId, httpServletRequest.getInputStream());
        log.info("File created");
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping (produces = "application/vnd.api+json", path = "/runs")
    public ResponseEntity<?> createRun(@RequestBody RunsData runsData) throws SchedulerException, ParseException {
        /*
        {
   "data":{
      "type":"runs",
      "attributes":{
         "refresh":true
      },
      "relationships":{
         "configuration-version":{
            "data":{
               "type":"configuration-versions",
               "id":"3ff92d0c-aefc-499d-8d0a-b72c9c4eecca"
            }
         },
         "workspace":{
            "data":{
               "type":"workspaces",
               "id":"6c4505fa-358e-491e-b021-63806d43264f"
            }
         }
      }
   }
}
         */
        log.info("Create new run");
        return ResponseEntity.status(201).body(remoteTfeService.createRun(runsData));
    }

    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/runs/{runId}")
    //public ResponseEntity<?> getRun(HttpEntity<String> httpEntity) {
    //    log.info("{}", httpEntity.getBody());
    public ResponseEntity<?> getRun(@PathVariable("runId") int runId) {
        /*
        {
   "data":{
      "type":"runs",
      "attributes":{
         "refresh":true
      },
      "relationships":{
         "configuration-version":{
            "data":{
               "type":"configuration-versions",
               "id":"3ff92d0c-aefc-499d-8d0a-b72c9c4eecca"
            }
         },
         "workspace":{
            "data":{
               "type":"workspaces",
               "id":"6c4505fa-358e-491e-b021-63806d43264f"
            }
         }
      }
   }
}
         */
        return ResponseEntity.ok(remoteTfeService.getRun(runId));
    }


    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/plans/{planId}")
    //public ResponseEntity<?> getRun(HttpEntity<String> httpEntity) {
    //    log.info("{}", httpEntity.getBody());
    public ResponseEntity<?> getPlan(@PathVariable("planId") int planId) {
        log.info("Checking plan data");
        return ResponseEntity.ok(remoteTfeService.getPlan(planId));
    }

    @Transactional
    @GetMapping (produces = "application/vnd.api+json", path = "/plans/{planId}/logs")
    //public ResponseEntity<?> getRun(HttpEntity<String> httpEntity) {
    //    log.info("{}", httpEntity.getBody());
    public ResponseEntity<?> getPlanLogs(@PathVariable("planId") int planId) throws IOException {
        return ResponseEntity.of(Optional.ofNullable(remoteTfeService.getPlanLogs(planId)));
    }

    @GetMapping(
            value = "configuration-versions/{planId}/terraformContent.tar.gz",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getTerraformPlanBinary(@PathVariable("planId") String planId) {
        return remoteTfeService.getContentFile(planId);
    }


}
