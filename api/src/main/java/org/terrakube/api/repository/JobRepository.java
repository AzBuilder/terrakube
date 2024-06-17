package org.terrakube.api.repository;

import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.workspace.Workspace;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findAllByStatus(JobStatus status);
    Optional<List<Job>> findByWorkspaceAndStatusNotInAndIdLessThan(Workspace workspace, List<JobStatus> jobStatuses, int jobId);
    Optional<Job> findFirstByWorkspaceAndAndStatusINOrderByIdAsc(Workspace workspace, List<JobStatus> jobStatuses);
}
