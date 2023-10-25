package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.terrakube.api.rs.workspace.history.archive.Archive;

import java.util.UUID;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, UUID> {
}
