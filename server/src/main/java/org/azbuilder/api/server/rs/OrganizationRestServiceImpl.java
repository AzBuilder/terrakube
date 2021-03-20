package org.azbuilder.api.server.rs;

import az.terrabot.api.organization.*;
import org.azbuilder.api.organization.OrganizationRestService;
import org.azbuilder.api.organization.model.OrganizationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class OrganizationRestServiceImpl implements OrganizationRestService {

    @Override
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        return null;
    }

    @Override
    public ResponseEntity<OrganizationDTO> getOrganizationById(String organizationId) {
        return null;
    }

    @Override
    public void addOrganization(@Valid OrganizationDTO organization) {

    }
}
