package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.terrakube.api.plugin.state.model.configuration.ConfigurationData;
import org.terrakube.api.plugin.state.model.configuration.ConfigurationModel;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementData;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementModel;
import org.terrakube.api.plugin.state.model.organization.OrganizationData;
import org.terrakube.api.plugin.state.model.organization.OrganizationModel;
import org.terrakube.api.plugin.state.model.state.StateData;
import org.terrakube.api.plugin.state.model.state.StateModel;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceData;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceModel;
import org.terrakube.api.plugin.storage.StorageTypeService;
import org.terrakube.api.repository.ContentRepository;
import org.terrakube.api.repository.HistoryRepository;
import org.terrakube.api.repository.OrganizationRepository;
import org.terrakube.api.repository.WorkspaceRepository;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.rs.workspace.content.Content;
import org.terrakube.api.rs.workspace.history.History;

import java.lang.module.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RemoteTfeService {
    @Autowired
    ContentRepository contentRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;
    @Autowired
    HistoryRepository historyRepository;

    @Value("${org.terrakube.hostname}")
    String hostname;

    StorageTypeService storageTypeService;

    EntitlementData getOrgEntitlementSet(String organizationName) {
        Organization organization = organizationRepository.getOrganizationByName(organizationName);
        if (organization != null) {
            EntitlementModel entitlementModel = new EntitlementModel();
            entitlementModel.setId("org-" + organizationName);
            Map<String, Object> entitlementAttributes = new HashMap();
            entitlementAttributes.put("operations", true);
            entitlementAttributes.put("private-module-registry", true);
            entitlementAttributes.put("sentinel", false);
            entitlementAttributes.put("run-tasks", false);
            entitlementAttributes.put("state-storage", true);
            entitlementAttributes.put("teams", false);
            entitlementAttributes.put("vcs-integrations", true);
            entitlementAttributes.put("usage-reporting", false);
            entitlementAttributes.put("user-limit", 5);
            entitlementAttributes.put("self-serve-billing", true);
            entitlementAttributes.put("audit-logging", false);
            entitlementAttributes.put("agents", false);
            entitlementAttributes.put("sso", false);
            entitlementModel.setAttributes(entitlementAttributes);
            entitlementModel.setType("entitlement-sets");
            EntitlementData entitlementData = new EntitlementData();
            entitlementData.setData(entitlementModel);

            log.info(entitlementData.toString());
            return entitlementData;
        } else {
            return null;
        }

    }

    OrganizationData getOrgInformation(String organizationName) {
        Organization organization = organizationRepository.getOrganizationByName(organizationName);
        if (organization != null) {
            OrganizationModel organizationModel = new OrganizationModel();
            organizationModel.setId(organizationName);
            organizationModel.setType("organizations");

            Map<String, Object> permissionMap = new HashMap();
            permissionMap.put("can-update", true);
            permissionMap.put("can-destroy", true);
            permissionMap.put("can-access-via-teams", false);
            permissionMap.put("can-create-module", false);
            permissionMap.put("can-create-team", false);
            permissionMap.put("can-create-workspace", true);
            permissionMap.put("can-manage-users", false);
            permissionMap.put("can-manage-subscription", false);
            permissionMap.put("can-manage-sso", false);
            permissionMap.put("can-update-oauth", false);
            permissionMap.put("can-update-sentinel", false);
            permissionMap.put("can-update-ssh-keys", false);
            permissionMap.put("can-update-api-token", false);
            permissionMap.put("can-traverse", false);
            permissionMap.put("can-start-trial", false);
            permissionMap.put("can-update-agent-pools", false);
            permissionMap.put("can-manage-tags", true);
            permissionMap.put("can-manage-public-modules", false);
            permissionMap.put("can-manage-public-providers", false);
            permissionMap.put("can-manage-run-tasks", true);
            permissionMap.put("can-read-run-tasks", true);
            permissionMap.put("can-create-provider", false);

            Map<String, Object> attributes = new HashMap();
            attributes.put("permissions", permissionMap);
            //attributes.put("external-id","org-"+organizationName);
            //attributes.put("created-at", "2020-03-26T22:13:38.456Z");
            //attributes.put("email", "user@example.com");
            //attributes.put("session-timeout", null);
            //attributes.put("session-remember", null);
            //attributes.put("collaborator-auth-policy", "password");
            //attributes.put("plan-expired", false);
            //attributes.put("plan-expires-at", null);
            //attributes.put("plan-is-trial", false);
            //attributes.put("plan-is-enterprise", false);
            //attributes.put("cost-estimation-enabled", false);
            //attributes.put("send-passing-statuses-for-untriggered-speculative-plans", false);
            //attributes.put("allow-force-delete-workspaces", false);
            attributes.put("name", organizationName);
            //attributes.put("fair-run-queuing-enabled", false);
            //attributes.put("saml-enabled", false);
            //attributes.put("owners-team-saml-role-id", null);
            //attributes.put("two-factor-conformant", false);
            //attributes.put("assessments-enforced", false);
            organizationModel.setAttributes(attributes);

            OrganizationData organizationData = new OrganizationData();
            organizationData.setData(organizationModel);
            return organizationData;
        } else {
            return null;
        }
    }

    WorkspaceData getWorkspace(String organizationName, String workspaceName, Map<String, Object> otherAttributes) {
        Optional<Workspace> workspace = Optional.ofNullable(workspaceRepository.getByOrganizationNameAndName(organizationName, workspaceName));

        if (workspace.isPresent()) {
            log.info("Found Workspace Id: {} Terraform: {}", workspace.get().getId().toString(), workspace.get().getTerraformVersion());
            WorkspaceData workspaceData = new WorkspaceData();

            WorkspaceModel workspaceModel = new WorkspaceModel();
            workspaceModel.setId(workspace.get().getId().toString());
            workspaceModel.setType("workspaces");
            Map<String, Object> attributes = new HashMap();
            attributes.put("name", workspaceName);
            attributes.put("terraform-version", workspace.get().getTerraformVersion());
            attributes.put("locked", workspace.get().isLocked());

            Map<String, Boolean> defaultAttributes = new HashMap();
            defaultAttributes.put("can-create-state-versions", true);
            defaultAttributes.put("can-destroy", true);
            defaultAttributes.put("can-force-unlock", true);
            defaultAttributes.put("can-lock", true);
            defaultAttributes.put("can-manage-run-tasks", true);
            defaultAttributes.put("can-manage-tags", true);
            defaultAttributes.put("can-queue-apply", true);
            defaultAttributes.put("can-queue-destroy", true);
            defaultAttributes.put("can-queue-run", true);
            defaultAttributes.put("can-read-settings", true);
            defaultAttributes.put("can-read-state-versions", true);
            defaultAttributes.put("can-read-variable", true);
            defaultAttributes.put("can-unlock", true);
            defaultAttributes.put("can-update", true);
            defaultAttributes.put("can-update-variable", true);
            defaultAttributes.put("can-read-assessment-result", true);
            defaultAttributes.put("can-force-delete", true);

            attributes.put("permissions", defaultAttributes);

            otherAttributes.forEach((key, value) -> {
                attributes.putIfAbsent(key, value);
            });


            workspaceModel.setAttributes(attributes);
            workspaceData.setData(workspaceModel);

            return workspaceData;
        } else {
            return null;
        }

    }

    WorkspaceData createWorkspace(String organizationName, WorkspaceData workspaceData) {
        Optional<Workspace> workspace = Optional.ofNullable(workspaceRepository.getByOrganizationNameAndName(organizationName, workspaceData.getData().getAttributes().get("name").toString()));

        if (workspace.isEmpty()) {
            log.info("Creating new workspace {} in {}", workspaceData.getData().getAttributes().get("name").toString(), organizationName);

            Organization organization = organizationRepository.getOrganizationByName(organizationName);
            Workspace newWorkspace = new Workspace();
            newWorkspace.setId(UUID.randomUUID());
            newWorkspace.setName(workspaceData.getData().getAttributes().get("name").toString());
            newWorkspace.setTerraformVersion(workspaceData.getData().getAttributes().get("terraform-version").toString());
            newWorkspace.setSource("empty");
            newWorkspace.setBranch("empty");
            newWorkspace.setOrganization(organization);
            workspaceRepository.save(newWorkspace);
        }
        Map<String, Object> otherAttributes = new HashMap();
        otherAttributes.put("locked", false);
        return getWorkspace(organizationName, workspaceData.getData().getAttributes().get("name").toString(), otherAttributes);
    }

    WorkspaceData updateWorkspaceLock(String workspaceId, boolean locked) {
        log.info("Update Lock Workspace: {} to {}", workspaceId, locked);
        Workspace workspace = workspaceRepository.getById(UUID.fromString(workspaceId));
        log.info("Workspace {} Organization {} ", workspace.getId().toString(), workspace.getOrganization().getId().toString());
        workspace.setLocked(locked);
        workspaceRepository.save(workspace);
        String organizationName = workspace.getOrganization().getName();
        Map<String, Object> otherAttributes = new HashMap();

        otherAttributes.put("locked", false);
        return getWorkspace(organizationName, workspace.getName(), otherAttributes);
    }

    StateData createWorkspaceState(String workspaceId, StateData stateData) {
        Workspace workspace = workspaceRepository.getById(UUID.fromString(workspaceId));
        History history = new History();
        UUID historyId = UUID.randomUUID();
        history.setId(historyId);
        history.setOutput(stateData.getData().getAttributes().get("state").toString());
        history.setWorkspace(workspace);
        historyRepository.save(history);

        StateData response = new StateData();
        response.setData(new StateModel());
        response.getData().setId(historyId.toString());
        response.getData().setType("state-versions");

        Map<String, Object> responseAttributes = new HashMap();
        responseAttributes.put("vcs-commit-sha", null);
        responseAttributes.put("vcs-commit-url", null);
        responseAttributes.put("hosted-state-download-url", "https://archivist.terraform.io/v1/object/4fde7951-93c0-4414-9a40-f3abc4bac490");
        responseAttributes.put("hosted-json-state-download-url", "https://archivist.terraform.io/v1/object/4fde7951-93c0-4414-9a40-f3abc4bac490");
        responseAttributes.put("serial", 1);
        response.getData().setAttributes(responseAttributes);
        return response;
    }

    ConfigurationData createConfigurationVersion(String workspaceId, ConfigurationData configurationData) {
        log.info("Create Configuration Version {}", configurationData.toString());
        log.info("Speculative {}", configurationData.getData().getAttributes().get("speculative"));
        log.info("Auto Queue Runs {}", configurationData.getData().getAttributes().get("auto-queue-runs"));
        UUID contentId = UUID.randomUUID();

        Content content = new Content();
        content.setId(contentId);
        content.setStatus("pending");
        content.setSource("tfe-api");
        content.setSpeculative((boolean) configurationData.getData().getAttributes().get("speculative"));
        content.setWorkspace(workspaceRepository.getById(UUID.fromString(workspaceId)));

        contentRepository.save(content);
        configurationData.getData().setId(contentId.toString());
        configurationData.getData().setType("configuration-versions");
        configurationData.getData().getAttributes().put("error", null);
        configurationData.getData().getAttributes().put("error-message", null);
        configurationData.getData().getAttributes().put("status", "pending");
        configurationData.getData().getAttributes().put("upload-url", String.format("https://%s/remote/tfe/v2/configuration-versions/%s", hostname, contentId));
        return configurationData;
    }

    ConfigurationData uploadFile(String contentId, MultipartFile multipartFile) {
        storageTypeService.createContentFile(contentId, multipartFile);
        Content content = contentRepository.getById(UUID.fromString(contentId));
        content.setStatus("uploaded");
        contentRepository.save(content);
        return searchConfiguration(contentId);
    }

    ConfigurationData searchConfiguration(String contentId) {
        Content content = contentRepository.getById(UUID.fromString(contentId));
        ConfigurationData configurationData = new ConfigurationData();
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setType("configuration-versions");
        configurationModel.setId(content.getId().toString());
        Map<String, Object> attributes = new HashMap();
        configurationData.getData().getAttributes().put("error", null);
        configurationData.getData().getAttributes().put("error-message", null);
        configurationData.getData().getAttributes().put("error-message", null);
        configurationData.getData().getAttributes().put("source", content.getSource());
        configurationData.getData().getAttributes().put("status", content.getStatus());
        configurationData.getData().getAttributes().put("speculative", content.isSpeculative());
        configurationData.getData().getAttributes().put("auto-queue-runs", content.isAutoQueueRuns());
        configurationModel.setAttributes(attributes);
        configurationData.setData(configurationModel);

        return configurationData;
    }


}
