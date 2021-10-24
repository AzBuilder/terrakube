package org.azbuilder.api.repository;

import org.azbuilder.api.rs.job.step.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepository extends JpaRepository<Step, Integer> {
}
