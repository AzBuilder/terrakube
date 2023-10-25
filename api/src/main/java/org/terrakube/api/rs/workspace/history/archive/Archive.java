package org.terrakube.api.rs.workspace.history.archive;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.terrakube.api.rs.workspace.history.History;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "temp_archive")
public class Archive {
    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private History history;
}
