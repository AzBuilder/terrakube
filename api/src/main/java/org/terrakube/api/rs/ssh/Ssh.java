package org.terrakube.api.rs.ssh;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.Organization;

import jakarta.persistence.*;
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
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
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
