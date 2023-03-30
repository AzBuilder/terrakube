package org.terrakube.executor.service.logs;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends CrudRepository<Logs, Integer> {

    Logs getBydIdAAndStepId(Integer id, String stepId);
}
