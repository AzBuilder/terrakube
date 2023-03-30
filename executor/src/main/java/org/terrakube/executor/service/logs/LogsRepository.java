package org.terrakube.executor.service.logs;

import org.springframework.data.repository.CrudRepository;

public interface LogsRepository extends CrudRepository<Logs, Integer> {

    Logs getByIdAndStepId(Integer id, String stepId);
}
