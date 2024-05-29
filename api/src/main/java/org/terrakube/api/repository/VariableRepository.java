package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.workspace.parameters.Variable;
import org.terrakube.api.rs.workspace.Workspace;

import java.util.List;
import java.util.UUID;

public interface VariableRepository extends JpaRepository<Variable, UUID> {
 
    List<Variable> findByWorkspace(Workspace organization);

}
