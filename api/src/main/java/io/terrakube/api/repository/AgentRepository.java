package io.terrakube.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.terrakube.api.rs.agent.Agent;

import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {
}
