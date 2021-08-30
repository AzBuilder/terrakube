package org.azbuilder.api.rs.workspace.parameters;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.ReadPermission;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.workspace.Workspace;
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

    @ReadPermission(expression = "svc is reading secrets")
    @Column(name="variable_value")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name="variable_category")
    private Category category;

    @Column(name="is_secret")
    private boolean isSecret;

    @ManyToOne
    private Workspace workspace;
}

enum Category{
    terraform,
    environment
}
