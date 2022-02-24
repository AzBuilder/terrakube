package org.azbuilder.api.repository;

import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findAllByStatus(JobStatus status);
}
