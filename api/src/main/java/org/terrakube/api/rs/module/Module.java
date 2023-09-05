package org.terrakube.api.rs.module;

import com.yahoo.elide.annotation.*;
import com.yahoo.elide.core.RequestScope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.ssh.Ssh;
import org.terrakube.api.rs.vcs.Vcs;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

@ReadPermission(expression = "team view module")
@CreatePermission(expression = "team manage module")
@UpdatePermission(expression = "team manage module OR user is a super service")
@DeletePermission(expression = "team manage module")
@Slf4j
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Module extends GenericAuditFields {
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "provider")
    private String provider;

    @Column(name = "source")
    private String source;

    @Column(name = "download_quantity")
    private int downloadQuantity = 0;

    @ManyToOne
    private Organization organization;

    @Exclude
    private static final ModuleCache moduleCache = new ModuleCache();

    @Transient
    @ComputedAttribute
    public String getRegistryPath(RequestScope requestScope) {
        return organization.getName() + "/" + name + "/" + provider;
    }

    @Transient
    @ComputedAttribute
    public List<String> getVersions(RequestScope requestScope) {
        return moduleCache.getVersions(getRegistryPath(requestScope), this.source, this.vcs, this.ssh);
    }

    @OneToOne
    private Vcs vcs;

    @OneToOne
    private Ssh ssh;
}
