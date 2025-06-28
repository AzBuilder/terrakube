package io.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.terrakube.api.rs.workspace.content.Content;

import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {

}
