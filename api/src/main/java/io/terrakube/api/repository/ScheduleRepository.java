package io.terrakube.api.repository;

import io.terrakube.api.rs.workspace.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
}
