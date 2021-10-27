package org.azbuilder.api.rs.job.step;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Include
@Getter
@Setter
@Entity
public class Step {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "step_number")
    private int stepNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "output")
    private String output;

    @ManyToOne
    private Job job;
}
