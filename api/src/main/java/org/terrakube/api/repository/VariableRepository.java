package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.workspace.parameters.Variable;
import org.terrakube.api.rs.workspace.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariableRepository extends JpaRepository<Variable, UUID> {
 
    Optional<List<Variable>> findByWorkspace(Workspace organization);

}
