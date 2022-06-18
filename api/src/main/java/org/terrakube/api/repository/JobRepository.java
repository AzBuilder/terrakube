package org.terrakube.api.repository;

import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findAllByStatus(JobStatus status);
}
