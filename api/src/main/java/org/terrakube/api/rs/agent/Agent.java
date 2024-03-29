package org.terrakube.api.rs.agent;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.terrakube.api.rs.Organization;

import javax.persistence.*;
import java.util.UUID;

@CreatePermission(expression = "user is a superuser")
@UpdatePermission(expression = "user is a superuser")
@DeletePermission(expression = "user is a superuser")
@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "agent")
public class Agent {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private Organization organization;
}
