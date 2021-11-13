package org.azbuilder.api.rs.template;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.plugin.security.audit.GenericAuditFields;
import org.azbuilder.api.rs.Organization;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@ReadPermission(expression = "team view template OR user is a service")
@CreatePermission(expression = "team manage template")
@UpdatePermission(expression = "team manage template")
@DeletePermission(expression = "team manage template")
@Include
@Getter
@Setter
@Entity
public class Template extends GenericAuditFields {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "version")
    private String version;

    @Column(name = "tcl")
    private String tcl;

    @ManyToOne
    private Organization organization;
}
