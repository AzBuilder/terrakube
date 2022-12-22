package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementData;
import org.terrakube.api.plugin.state.model.entitlement.EntitlementModel;
import org.terrakube.api.plugin.state.model.organization.OrganizationData;
import org.terrakube.api.plugin.state.model.organization.OrganizationModel;
import org.terrakube.api.plugin.state.model.workspace.WorkspaceModel;
import org.terrakube.api.repository.OrganizationRepository;
import org.terrakube.api.rs.Organization;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class RemoteTfeService {

    OrganizationRepository organizationRepository;

    EntitlementData getOrgEntitlementSet(String organizationName) {
        Organization organization = organizationRepository.getOrganizationByName(organizationName);
        if (organization != null) {
            EntitlementModel entitlementModel = new EntitlementModel();
            entitlementModel.setId("org-"+organizationName);
            Map<String, String> entitlementAttributes = new HashMap();
            entitlementAttributes.put("operations", "true");
            entitlementAttributes.put("private-module-registry", "true");
            entitlementAttributes.put("sentinel", "false");
            entitlementAttributes.put("run-tasks", "false");
            entitlementAttributes.put("state-storage", "true");
            entitlementAttributes.put("teams", "false");
            entitlementAttributes.put("vcs-integrations", "true");
            entitlementAttributes.put("usage-reporting", "false");
            entitlementAttributes.put("user-limit", "5");
            entitlementAttributes.put("self-serve-billing", "true");
            entitlementAttributes.put("audit-logging", "false");
            entitlementAttributes.put("agents", "false");
            entitlementAttributes.put("sso", "false");
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

            Map<String, Boolean> permissionMap = new HashMap();
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

    WorkspaceModel createWorkspace() {
        return null;
    }


}
