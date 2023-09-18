package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.token.group.Group;

import java.util.List;
import java.util.UUID;

public interface TeamTokenRepository extends JpaRepository<Group, UUID> {

    List<Group> findByGroupIn(List<String> groups);
}
