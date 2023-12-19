package org.terrakube.api.rs.job.step;

import com.yahoo.elide.annotation.Exclude;
import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.hibernate.annotations.Type;
import org.terrakube.api.rs.job.LogStatus;

import javax.persistence.*;
import java.util.UUID;

@Include
@Getter
@Setter
@Entity(name = "step")
public class Step {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "step_number")
    private int stepNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Exclude
    @Column(name = "log_status")
    @Enumerated(EnumType.STRING)
    private LogStatus logStatus;

    @Column(name = "output")
    private String output;

    @ManyToOne
    private Job job;
}
