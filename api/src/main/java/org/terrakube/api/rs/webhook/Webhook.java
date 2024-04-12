package org.terrakube.api.rs.webhook;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import org.terrakube.api.rs.IdConverter;

import java.sql.Types;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "webhook")
public class Webhook {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private WebhookType type;

    @Column(name="template_mapping")
    private String templateMapping;

    @Column(name="reference_id")
    private String referenceId;

    @Column(name="remote_hook_id")
    private String remoteHookId;
}

