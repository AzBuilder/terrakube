package org.azbuilder.api.rs.team;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@CreatePermission(expression = "user is a superuser")
@UpdatePermission(expression = "user is a superuser")
@DeletePermission(expression = "user is a superuser")
@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Team {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "manage_workspace")
    private boolean manageWorkspace;

    @Column(name = "manage_module")
    private boolean manageModule;

    @Column(name = "manage_provider")
    private boolean manageProvider;

    @ManyToOne
    private Organization organization;
}
