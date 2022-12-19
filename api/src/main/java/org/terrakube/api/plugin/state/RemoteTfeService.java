package org.terrakube.api.plugin.state;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.state.model.organization.EntitlementModel;
import org.terrakube.api.plugin.state.model.organization.EntitlementAttributes;
import org.terrakube.api.plugin.state.model.organization.OrganizationAttributes;
import org.terrakube.api.plugin.state.model.organization.OrganizationModel;
import org.terrakube.api.repository.OrganizationRepository;
import org.terrakube.api.rs.Organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class RemoteTfeService {

    OrganizationRepository organizationRepository;

    EntitlementModel getOrgEntitlementSet(String organizationName){
        Organization organization = organizationRepository.getOrganizationByName(organizationName);
        if(organization != null){
            EntitlementModel entitlementModel = new EntitlementModel();
            entitlementModel.setId(UUID.randomUUID().toString());
            EntitlementAttributes entitlementAttributes = new EntitlementAttributes();
            entitlementAttributes.setOperations(true);
            entitlementAttributes.setPrivateModuleRegistry(true);
            entitlementAttributes.setSentinel(false);
            entitlementAttributes.setStateStorage(true);
            entitlementAttributes.setTeams(false);
            entitlementAttributes.setVCSIntegrations(true);
            entitlementModel.setAttributes(entitlementAttributes);
            entitlementModel.setId(UUID.randomUUID().toString());
            entitlementModel.setType("entitlement-sets");
            return entitlementModel;
        } else {
            return null;
        }

    }

    OrganizationModel getOrgInformation(String organizationName){
        Organization organization = organizationRepository.getOrganizationByName(organizationName);
        if(organization != null){
            OrganizationModel organizationModel = new OrganizationModel();
            organizationModel.setId(organizationName);
            organizationModel.setType("organizations");
            OrganizationAttributes organizationAttributes = new OrganizationAttributes();
            organizationAttributes.setName(organizationName);
            Map<String, Boolean> attributeList = new HashMap();
            attributeList.put("can-update",true);
            attributeList.put("can-destroy",true);
            attributeList.put("can-access-via-teams",false);
            attributeList.put("can-create-module",false);
            attributeList.put("can-create-team",false);
            attributeList.put("can-create-workspace",true);
            attributeList.put("can-manage-users",false);
            attributeList.put("can-manage-subscription",false);
            attributeList.put("can-manage-sso",false);
            attributeList.put("can-update-oauth",false);
            attributeList.put("can-update-sentinel",false);
            attributeList.put("can-update-ssh-keys",false);
            attributeList.put("can-update-api-token",false);
            attributeList.put("can-traverse",false);
            attributeList.put("can-start-trial",false);
            attributeList.put("can-update-agent-pools",false);
            attributeList.put("can-manage-tags",true);
            attributeList.put("can-manage-public-modules",false);
            attributeList.put("can-manage-public-providers",false);
            attributeList.put("can-manage-run-tasks",true);
            attributeList.put("can-read-run-tasks",true);
            attributeList.put("can-create-provider",false);
            organizationAttributes.setPermissions(attributeList);
            return organizationModel;
        } else {
            return null;
        }
    }
}
