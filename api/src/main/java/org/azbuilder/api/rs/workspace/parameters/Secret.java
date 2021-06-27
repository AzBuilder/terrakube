package org.azbuilder.api.rs.workspace.parameters;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.workspace.Workspace;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;


@Include
@Getter
@Setter
@Entity
public class Secret {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name="secret_key")
    private String key;

    @Column(name="secret_value")
    private String value;

    @ManyToOne
    private Workspace workspace;
}
