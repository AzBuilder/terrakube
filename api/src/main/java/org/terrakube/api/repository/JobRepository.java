package org.terrakube.api.repository;

import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.job.Job;
import org.terrakube.api.rs.job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.terrakube.api.rs.workspace.Workspace;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findAllByOrganizationAndStatusNotInOrderByIdAsc(Organization organization, List<JobStatus> status);
    List<Job> findAllByStatusInOrderByIdAsc(List<JobStatus> status);

    Optional<List<Job>> findAllByWorkspaceAndStatusNotInOrderByIdAsc(Workspace workspace, List<JobStatus> status);
    List<Job> findAllByWorkspaceAndStatusInOrderByIdDesc(Workspace workspace, List<JobStatus> jobStatuses);
    Optional<List<Job>> findByWorkspaceAndStatusNotInAndIdLessThan(Workspace workspace, List<JobStatus> jobStatuses, int jobId);

    Optional<Job> findFirstByWorkspaceAndAndStatusInOrderByIdAsc(Workspace workspace, List<JobStatus> jobStatuses);
    Optional<Job> findFirstByWorkspaceAndStatusInOrderByIdAsc(Workspace workspace, List<JobStatus> jobStatuses);
}
