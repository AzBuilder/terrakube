package io.terrakube.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import io.terrakube.api.rs.team.Team;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findAllByOrganizationIdAndNameIn(UUID organizationId, List<String> names);
}
