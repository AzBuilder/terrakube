package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.rs.workspace.tag.WorkspaceTag;

import java.util.UUID;

public interface WorkspaceTagRepository extends JpaRepository<WorkspaceTag, UUID> {

    WorkspaceTag getByWorkspaceAndTagId(Workspace workspace, String tagId);

    void deleteByWorkspace(Workspace workspace);
}
