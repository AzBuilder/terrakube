package org.azbuilder.api.rs.workspace.history;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.plugin.security.audit.GenericAuditFields;
import org.azbuilder.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class History extends GenericAuditFields {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "output")
    private String output;

    @ManyToOne
    private Workspace workspace;
}
