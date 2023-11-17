package org.terrakube.api.rs.webhook;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Webhook {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
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

