package io.terrakube.api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import io.terrakube.api.rs.webhook.Webhook;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {
}
