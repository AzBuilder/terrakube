package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.token.pat.Pat;

import java.util.List;
import java.util.UUID;

public interface PatRepository extends JpaRepository<Pat, UUID> {

    List<Pat> findByCreatedBy(String createdBy);
}
