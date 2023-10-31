package org.terrakube.api.rs.workspace.history;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;
import org.terrakube.api.rs.workspace.history.archive.Archive;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(rootLevel = false)
@CreatePermission(expression = "user is a super service")
@UpdatePermission(expression = "user is a super service")
@DeletePermission(expression = "user is a super service")
@Getter
@Setter
@Entity
public class History extends GenericAuditFields {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "job_reference")
    private String jobReference;

    @Column(name = "output")
    private String output;

    @Column(name = "serial")
    private int serial = 1;

    @Column(name = "md5")
    private String md5 = "";

    @Column(name = "lineage")
    private String lineage = "";

    @ManyToOne
    private Workspace workspace;

    @Exclude
    @OneToMany(mappedBy = "history")
    private List<Archive> archive;
}
