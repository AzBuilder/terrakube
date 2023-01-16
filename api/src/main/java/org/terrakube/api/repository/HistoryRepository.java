package org.terrakube.api.repository;

import org.terrakube.api.rs.workspace.history.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoryRepository extends JpaRepository<History, UUID> {

}
