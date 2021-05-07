package org.azbuilder.api.rs.module;


import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(type = "module")
@Getter
@Setter
@Entity
public class Module {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "module")
    private List<Definition> definition;
}
