package org.terrakube.api.plugin.importer.tfcloud.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.terrakube.api.plugin.importer.tfcloud.StateVersion;
import org.terrakube.api.plugin.importer.tfcloud.VariableResponse;
import org.terrakube.api.plugin.importer.tfcloud.VariableResponse.VariableData;
import org.terrakube.api.plugin.importer.tfcloud.VariableResponse.VariableData.VariableAttributes;
import org.terrakube.api.plugin.importer.tfcloud.WorkspaceImport;
import org.terrakube.api.plugin.importer.tfcloud.WorkspaceImportRequest;
import org.terrakube.api.plugin.importer.tfcloud.WorkspaceListResponse;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.terrakube.api.repository.HistoryRepository;
import org.terrakube.api.repository.OrganizationRepository;
import org.terrakube.api.repository.VariableRepository;
import org.terrakube.api.repository.VcsRepository;
import org.terrakube.api.repository.WorkspaceRepository;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.rs.workspace.history.History;
import org.terrakube.api.rs.workspace.parameters.Category;
import org.terrakube.api.rs.workspace.parameters.Variable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkspaceService {

    private final RestTemplate restTemplate;
    WorkspaceRepository workspaceRepository;
    HistoryRepository historyRepository;
    VcsRepository vcsRepository;
    OrganizationRepository organizationRepository;
    VariableRepository variableRepository;
    private StorageTypeService storageTypeService;
    private String hostname;

    public WorkspaceService(@Value("${org.terrakube.hostname}") String hostname,
            WorkspaceRepository workspaceRepository,
            HistoryRepository historyRepository,
            StorageTypeService storageTypeService,
            VcsRepository vcsRepository,
            OrganizationRepository organizationRepository,
            VariableRepository variableRepositor) {
        this.restTemplate = new RestTemplate();
        this.workspaceRepository = workspaceRepository;
        this.historyRepository = historyRepository;
        this.storageTypeService = storageTypeService;
        this.vcsRepository = vcsRepository;
        this.organizationRepository = organizationRepository;
        this.variableRepository = variableRepositor;
        this.hostname = hostname;
    }

    public class NullResponseException extends RuntimeException {
        public NullResponseException(String message) {
            super(message);
        }
    }

    private <T> T makeRequest(String apiToken, String url, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.setContentType(MediaType.valueOf("application/vnd.api+json"));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    public List<WorkspaceImport.WorkspaceData> getWorkspaces(String apiToken, String apiUrl, String organization) {
        List<WorkspaceImport.WorkspaceData> allData = new ArrayList<>();
        int currentPage = 1;

        while (true) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .pathSegment("organizations")
                    .pathSegment(organization)
                    .pathSegment("workspaces");

            String url = builder.toUriString() + "?page[size]=50&page[number]=" + currentPage;
            log.info("url: {}", url);
            WorkspaceListResponse response = makeRequest(apiToken, url, WorkspaceListResponse.class);

            if (response == null || response.getData() == null) {
                break;
            } else {
                allData.addAll(response.getData());
            }

            if (response.getMeta().getPagination().getNextPage() == null) {
                break;
            }

            currentPage++;
        }

        return allData;
    }

    public List<VariableAttributes> getVariables(String apiToken, String apiUrl, String organizationName,
            String workspaceName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("workspaces")
                .pathSegment(workspaceName)
                .pathSegment("vars");

        String url = builder.toUriString();
        VariableResponse response = makeRequest(apiToken, url, VariableResponse.class);
        if (response != null) {
            return response.getData().stream()
                    .map(VariableData::getAttributes)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    public StateVersion.Attributes getCurrentState(String apiToken, String apiUrl, String workspaceId) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .pathSegment("workspaces")
                    .pathSegment(workspaceId)
                    .pathSegment("current-state-version");

            String url = builder.toUriString();
            StateVersion stateVersionResponse = makeRequest(apiToken, url, StateVersion.class);
            if (stateVersionResponse != null && stateVersionResponse.getData() != null) {
                return stateVersionResponse.getData().getAttributes();
            } else {
                throw new NullResponseException("Error: Response from State is null");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            log.info("State not found for workspace: {}", workspaceId);
            return null;
        }
    }

    public Resource downloadState(String apiToken, String stateUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);

        ResponseEntity<Resource> response = restTemplate.exchange(
                stateUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Resource.class);

        if (response != null && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new NullResponseException("Error: Response from State is null");
        }
    }

    public boolean importWorkspace(String apiToken, String apiUrl, WorkspaceImportRequest workspaceImportRequest) {

        Workspace workspace = createWorkspaceFromRequest(workspaceImportRequest);
        workspace = workspaceRepository.save(workspace);
        List<VariableAttributes> variablesImporter = getVariables(
                apiToken,
                apiUrl,
                workspaceImportRequest.getOrganization(),
                workspaceImportRequest.getId());

        importVariables(variablesImporter, workspace);

        StateVersion.Attributes lastState = getCurrentState(
                apiToken,
                apiUrl,
                workspaceImportRequest.getId());

        if (lastState == null) {
            return true;
        }

        String stateDownloadUrl = lastState.getHostedStateDownloadUrl();
        String stateDownloadJsonUrl = lastState.getHostedJsonStateDownloadUrl();

        History history = new History();
        history.setWorkspace(workspace);
        history.setSerial(1);
        history.setMd5("0");
        history.setLineage("0");
        history.setOutput("");
        historyRepository.save(history);

        history.setOutput(String
                .format("https://%s/tfstate/v1/organization/%s/workspace/%s/state/%s.json",
                        hostname,
                        workspace.getOrganization().getId().toString(),
                        workspace.getId().toString(),
                        history.getId().toString()));
        historyRepository.save(history);

        try {
            Resource stateJson = downloadState(apiToken, stateDownloadJsonUrl);
            Resource state = downloadState(apiToken, stateDownloadUrl);
            String terraformStateJson = "";
            String terraformState = "";
            try {
                terraformStateJson = readResourceToString(stateJson);
                terraformState = readResourceToString(state);

            } catch (IOException e) {
                log.error(e.getMessage());
                return false;
            }
            storageTypeService.uploadTerraformStateJson(workspace.getOrganization().getId().toString(),
                    workspace.getId().toString(), terraformStateJson, history.getId().toString());

            storageTypeService.uploadState(workspace.getOrganization().getId().toString(),
                    workspace.getId().toString(), terraformState);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    private String readResourceToString(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private Workspace createWorkspaceFromRequest(WorkspaceImportRequest workspaceImportRequest) {
        Workspace workspace = new Workspace();
        workspace.setName(workspaceImportRequest.getName());
        workspace.setDescription(workspaceImportRequest.getDescription());
        workspace.setTerraformVersion(workspaceImportRequest.getTerraformVersion());
        workspace.setExecutionMode(workspaceImportRequest.getExecutionMode().equals("local") ? "local" : "remote"); //Tfcloud supports agent execution mode, but Terrakkube not

        // If the workspace has a VCS, set it
        log.info("VCS ID: {}", workspaceImportRequest.getVcsId());
        if (workspaceImportRequest.getVcsId() != null && !workspaceImportRequest.getVcsId().isEmpty()) {
            UUID vcsId = UUID.fromString(workspaceImportRequest.getVcsId());
            vcsRepository.findById(vcsId).ifPresent(workspace::setVcs);
            // if branch is not set, set it to main
            workspace.setBranch(
                    workspaceImportRequest.getBranch() == null ? "main" : workspaceImportRequest.getBranch());
            workspace.setFolder(workspaceImportRequest.getFolder());
            workspace.setSource(workspaceImportRequest.getSource());
        } else {
            workspace.setBranch("remote-content");
            workspace.setSource("empty");
        }

        // Set the organization
        organizationRepository.findById(UUID.fromString(workspaceImportRequest.getOrganizationId()))
                .ifPresent(workspace::setOrganization);
        workspace.setIacType("terraform");

        return workspace;
    }

    private void importVariables(List<VariableAttributes> variablesImporter, Workspace workspace) {
        for (VariableAttributes variableAttribute : variablesImporter) {
            Variable variable = new Variable();
            variable.setKey(variableAttribute.getKey());
            variable.setValue(variableAttribute.getValue() != null ? variableAttribute.getValue() : "");
            variable.setDescription(variableAttribute.getDescription());
            variable.setSensitive(variableAttribute.isSensitive());
            variable.setCategory("env".equals(variableAttribute.getCategory()) ? Category.ENV : Category.TERRAFORM);
            variable.setHcl(variableAttribute.isHcl());
            variable.setWorkspace(workspace);
            variableRepository.save(variable);
        }
    }
}
