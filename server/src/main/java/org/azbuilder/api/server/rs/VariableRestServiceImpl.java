package org.azbuilder.api.server.rs;

import org.azbuilder.api.variable.VariableRestService;
import org.azbuilder.api.variable.model.VariableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class VariableRestServiceImpl implements VariableRestService {
    @Override
    public ResponseEntity<List<VariableDTO>> getAllVariables(String organizationId, String workspaceId) {
        return null;
    }

    @Override
    public ResponseEntity<VariableDTO> getVariableById(String organizationId, String workspaceId, String variableId) {
        return null;
    }

    @Override
    public void addVariable(String organizationId, String workspaceId, @Valid VariableDTO variableDTO) {

    }
}
