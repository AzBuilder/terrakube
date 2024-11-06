package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.collection.CollectionReference;

import java.util.UUID;

public interface CollectionReferenceRepository extends JpaRepository<CollectionReference, UUID> {
}
