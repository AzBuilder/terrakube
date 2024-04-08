package org.terrakube.api.rs.workspace.content;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.workspace.Workspace;

import jakarta.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "content")
public class Content {
    @Id
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Workspace workspace;

    @Column(name = "auto_queue_runs")
    private boolean autoQueueRuns;

    @Column(name = "speculative")
    private boolean speculative;

    @Column(name = "status")
    private String status;

    @Column(name = "source")
    private String source;
}
