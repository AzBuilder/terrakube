package org.terrakube.api.rs.collection;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.workspace.Workspace;

import java.sql.Types;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "collection_workspace_reference")
public class CollectionReference {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Collection collection;

    @ManyToOne
    private Workspace workspace;
}
