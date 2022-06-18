package org.terrakube.api.repository;

import org.terrakube.api.rs.vcs.Vcs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VcsRepository extends JpaRepository<Vcs, UUID> {
}
