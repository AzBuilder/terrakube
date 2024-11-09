package org.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.agent.Agent;

import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
}
