package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.workspace.parameters.Variable;

import java.util.UUID;

public interface VariableRepository extends JpaRepository<Variable, UUID> {

}
