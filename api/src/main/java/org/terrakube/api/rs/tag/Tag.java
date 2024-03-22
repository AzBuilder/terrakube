package org.terrakube.api.rs.tag;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "tag")
public class Tag extends GenericAuditFields {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private Organization organization;

}
