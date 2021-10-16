package org.azbuilder.api.rs.job.step;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;

import javax.persistence.*;

@Include(rootLevel = false)
@Getter
@Setter
@Entity
public class Step {

    @Id
    private int id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "output")
    private String output;

    @ManyToOne
    private Job job;
}
