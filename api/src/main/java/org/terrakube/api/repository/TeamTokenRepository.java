package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.token.team.Team;

import java.util.List;
import java.util.UUID;

public interface TeamTokenRepository extends JpaRepository<Team, UUID> {

    List<Team> findByGroupIn(List<String> groups);
}
