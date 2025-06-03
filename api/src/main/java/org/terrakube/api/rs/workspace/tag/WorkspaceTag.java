package org.terrakube.api.rs.workspace.tag;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;

import jakarta.persistence.*;

import java.sql.Types;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "workspacetag")
public class WorkspaceTag extends GenericAuditFields {
    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tag_id")
    private String tagId;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

}
