package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.workspace.tag.WorkspaceTag;

import java.util.UUID;

public interface WorkspaceTagRepository extends JpaRepository<WorkspaceTag, UUID> {
}
