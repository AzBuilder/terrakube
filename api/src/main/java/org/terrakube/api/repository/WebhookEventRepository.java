package org.terrakube.api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.webhook.Webhook;
import org.terrakube.api.rs.webhook.WebhookEvent;
import org.terrakube.api.rs.webhook.WebhookEventType;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    List<WebhookEvent> findByWebhookAndEventOrderByPriorityAsc(Webhook webhook, WebhookEventType event);
}
