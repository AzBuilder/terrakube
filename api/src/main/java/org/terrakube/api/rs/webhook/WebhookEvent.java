package org.terrakube.api.rs.webhook;

import java.sql.Types;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.IdConverter;

import com.yahoo.elide.annotation.Include;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Include
@Entity(name = "webhook_event")
public class WebhookEvent extends GenericAuditFields {
    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    private UUID id;

    private String branch;

    private String path;

    @Column(name = "template_id")
    private String templateId;
    
    @Enumerated(EnumType.STRING)
    private WebhookEventType event;
    
    private int priority;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Webhook webhook;
}