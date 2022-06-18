package org.terrakube.api.rs.workspace.parameters;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.ReadPermission;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Variable {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name="variable_key")
    private String key;

    @ReadPermission(expression = "user read secret")
    @Column(name="variable_value")
    private String value;

    @Column(name="variable_description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="variable_category")
    private Category category;

    @Column(name="sensitive")
    private boolean sensitive;

    @Column(name="hcl")
    private boolean hcl;

    @ManyToOne
    private Workspace workspace;
}

