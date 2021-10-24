package org.azbuilder.api.repository;

import org.azbuilder.api.rs.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
}
