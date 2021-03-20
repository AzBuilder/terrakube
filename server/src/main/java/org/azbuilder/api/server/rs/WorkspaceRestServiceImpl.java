package org.azbuilder.api.server.rs;

import org.azbuilder.api.workspace.WorkspaceRestService;
import org.azbuilder.api.workspace.model.WorkspaceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class WorkspaceRestServiceImpl implements WorkspaceRestService {
    @Override
    public ResponseEntity<List<WorkspaceDTO>> getAllWorkspaces(String organizationId) {
        return null;
    }

    @Override
    public ResponseEntity<WorkspaceDTO> getWorkspaceById(String organizationId, String workspaceId) {
        return null;
    }

    @Override
    public void addOrganization(String organizationId, @Valid WorkspaceDTO workspaceDTO) {

    }
}
