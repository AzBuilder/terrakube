package org.azbuilder.api.rs.vcs;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;


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
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="vcs_type")
    private VcsType vcsType;

    @Column(name = "description")
    private String description;

    @Column(name = "client_id")
    private String clientId;

    @ReadPermission(expression = "service read vcs secret")
    @Column(name = "client_secret")
    private String clientSecret;

    @ReadPermission(expression = "service read vcs secret")
    @Column(name = "access_token")
    private String accessToken;

    @ManyToOne
    private Organization organization;

}
