package org.azbuilder.api.rs.provider;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;
import org.azbuilder.api.rs.provider.implementation.Implementation;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Provider {
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "provider")
    private List<Implementation> implementation;
}
