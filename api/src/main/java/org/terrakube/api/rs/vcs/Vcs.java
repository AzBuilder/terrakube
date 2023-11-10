package org.terrakube.api.rs.vcs;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.Organization;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@ReadPermission(expression = "team view vcs")
@CreatePermission(expression = "team manage vcs")
@UpdatePermission(expression = "team manage vcs")
@DeletePermission(expression = "team manage vcs")
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Vcs extends GenericAuditFields {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "vcs_type")
    private VcsType vcsType;

    @Column(name = "description")
    private String description;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "callback")
    private String callback;

    @Column(name = "endpoint")
    private String endpoint;

    @ReadPermission(expression = "read vcs secret")
    @Column(name = "client_secret")
    private String clientSecret;

    @UpdatePermission(expression = "user is a super service")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VcsStatus status = VcsStatus.PENDING;

    @ReadPermission(expression = "read access token")
    @Column(name = "access_token")
    private String accessToken;

    @Exclude
    @Column(name = "refresh_token")
    private String refreshToken;

    @Exclude
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "token_expiration")
    private Date tokenExpiration;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @ManyToOne
    private Organization organization;

}
