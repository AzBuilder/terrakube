package org.azbuilder.api.rs.vcs;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.hooks.vcs.VcsReadTokenHook;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@Slf4j
@ReadPermission(expression = "team view vcs OR user is a service")
@CreatePermission(expression = "team manage vcs")
@UpdatePermission(expression = "team manage vcs")
@DeletePermission(expression = "team manage vcs")
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Vcs {

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

    @ReadPermission(expression = "service read vcs secret")
    @Column(name = "client_secret")
    private String clientSecret;

    @ReadPermission(expression = "service read vcs secret")
    @LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.READ, phase = LifeCycleHookBinding.TransactionPhase.PRESECURITY, hook = VcsReadTokenHook.class)
    @Column(name = "access_token")
    private String accessToken;

    @Exclude
    @Column(name = "refresh_token")
    private String refreshToken;

    @Exclude
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "token_expiration")
    private Date tokenExpiration;

    @ManyToOne
    private Organization organization;

}
