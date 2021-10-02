package org.azbuilder.api.repository;

import org.azbuilder.api.rs.vcs.Vcs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VcsRepository extends JpaRepository<Vcs, UUID> {
}
