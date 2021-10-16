package org.azbuilder.api.repository;

import org.azbuilder.api.rs.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, Integer> {
}
