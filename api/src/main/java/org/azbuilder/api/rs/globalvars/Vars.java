package org.azbuilder.api.rs.globalvars;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.workspace.parameters.Category;
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
public class Vars {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name="variable_key")
    private String key;

    @ReadPermission(expression = "admin read secret")
    @Column(name="variable_value")
    private String value;

    @Column(name="variable_description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="variable_category")
    private Category category;

    @UpdatePermission(expression = "admin update secret")
    @Column(name="sensitive")
    private boolean sensitive;

    @Column(name="hcl")
    private boolean hcl;
}
