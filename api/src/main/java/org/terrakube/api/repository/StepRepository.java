package org.terrakube.api.repository;

import org.terrakube.api.rs.job.step.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StepRepository extends JpaRepository<Step, UUID> {

    List<Step> findByJobId(int jobId);

    Optional<Step> findFirstByJobIdOOrderByStepNumber(int jobId);
}
