package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.terrakube.api.rs.workspace.content.Content;

import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {

}
