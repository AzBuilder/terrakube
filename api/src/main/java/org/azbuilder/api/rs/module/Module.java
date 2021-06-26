package org.azbuilder.api.rs.module;


import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.Organization;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Module {
    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    @ManyToOne
    private Organization organization = null;

    @OneToMany(mappedBy = "module")
    private List<Definition> definition = new ArrayList<>();
}
