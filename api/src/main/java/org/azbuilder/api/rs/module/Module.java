package org.azbuilder.api.rs.module;

import com.yahoo.elide.annotation.*;
import com.yahoo.elide.core.RequestScope;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.vcs.Vcs;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ReadPermission(expression = "team view module OR user is a service")
@CreatePermission(expression = "team manage module")
@UpdatePermission(expression = "user is a service OR team manage module")
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

    @Column(name = "download_quantity")
    private int downloadQuantity = 0;

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
            CredentialsProvider credentialsProvider = null;
            if (vcs != null) {
                log.info("vcs using {}", vcs.getVcsType());
                switch (vcs.getVcsType()) {
                    case GITHUB:
                        credentialsProvider = new UsernamePasswordCredentialsProvider(vcs.getAccessToken(), "");
                        break;
                    case BITBUCKET:
                        credentialsProvider = new UsernamePasswordCredentialsProvider("x-token-auth", vcs.getAccessToken());
                        break;
                    case GITLAB:
                        credentialsProvider = new UsernamePasswordCredentialsProvider("oauth2", vcs.getAccessToken());
                        break;
                    case AZURE_DEVOPS:
                        credentialsProvider = new UsernamePasswordCredentialsProvider("dummy", vcs.getAccessToken());
                        break;
                    default:
                        credentialsProvider = null;
                        break;
                }
            }
            Map<String, Ref> tags = Git.lsRemoteRepository()
                    .setTags(true)
                    .setRemote(source)
                    .setCredentialsProvider(credentialsProvider)
                    .callAsMap();
            tags.forEach((key, value) -> {
                versionList.add(key.replace("refs/tags/", ""));
            });
        } catch (GitAPIException e) {
            log.error(e.getMessage());
        }
        return versionList;
    }

    @OneToOne
    private Vcs vcs;
}
