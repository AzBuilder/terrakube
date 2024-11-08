package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.collection.Reference;

import java.util.UUID;

public interface ReferenceRepository extends JpaRepository<Reference, UUID> {
}
