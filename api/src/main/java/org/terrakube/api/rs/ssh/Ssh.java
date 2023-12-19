package org.terrakube.api.rs.ssh;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.Organization;

import javax.persistence.*;
import java.util.UUID;

@ReadPermission(expression = "team view ssh")
@CreatePermission(expression = "team manage ssh")
@UpdatePermission(expression = "team manage ssh")
@DeletePermission(expression = "team manage ssh")
@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "ssh")
public class Ssh extends GenericAuditFields {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ReadPermission(expression = "read private key")
    @Column(name = "private_key")
    private String privateKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "ssh_type")
    private SshType sshType;

    @ManyToOne
    private Organization organization;
}
