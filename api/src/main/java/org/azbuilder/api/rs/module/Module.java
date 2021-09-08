package org.azbuilder.api.rs.module;

import com.yahoo.elide.annotation.*;
import com.yahoo.elide.core.RequestScope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.rs.Organization;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ReadPermission(expression = "team view module OR user is a service")
@CreatePermission(expression = "team manage module")
@UpdatePermission(expression = "team manage module")
@DeletePermission(expression = "team manage module")
@Slf4j
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Module {
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

    @Column(name = "source_sample")
    private String sourceSample;

    @ManyToOne
    private Organization organization;

    @Transient
    @ComputedAttribute
    public String getRegistryPath(RequestScope requestScope) {
        return organization.getName() + "/" + name + "/" + provider;
    }

    @Transient
    @ComputedAttribute
    public List<String> getVersions(RequestScope requestScope) {
        List<String> versionList = new ArrayList<>();
        try {
            Map<String, Ref> tags = Git.lsRemoteRepository()
                    .setTags(true)
                    .setRemote(source)
                    .callAsMap();
            tags.forEach((key, value) -> {
                versionList.add(key.replace("refs/tags/", ""));
            });
        } catch (GitAPIException e) {
            log.error(e.getMessage());
        }
        return versionList;
    }
}
