package io.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.terrakube.api.rs.workspace.history.History;
import io.terrakube.api.rs.workspace.history.archive.Archive;
import io.terrakube.api.rs.workspace.history.archive.ArchiveType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, UUID> {

    Optional<Archive> findByHistoryAndType(History history, ArchiveType archiveType);
}
