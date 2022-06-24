package org.terrakube.api.repository;

import org.terrakube.api.rs.job.step.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Integer> {

    List<Step> findByJobId(int jobId);
}
