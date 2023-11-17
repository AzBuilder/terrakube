package org.terrakube.api.repository;

import org.terrakube.api.rs.webhook.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {
}
