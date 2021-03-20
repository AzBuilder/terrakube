package org.azbuilder.api.server.rs;

import org.azbuilder.api.module.ModuleRestService;
import org.azbuilder.api.module.model.ModuleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ModuleRestServiceImpl implements ModuleRestService {
    @Override
    public ResponseEntity<List<ModuleDTO>> getAllModules(String organizationId) {
        return null;
    }

    @Override
    public ResponseEntity<ModuleDTO> getModuleById(String organizationId, String moduleId) {
        return null;
    }

    @Override
    public void addModule(String organizationId, @Valid ModuleDTO moduleDTO) {

    }
}
