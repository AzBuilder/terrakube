package org.terrakube.api.plugin.streaming;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends CrudRepository<Logs, Integer> {

    Logs getBydIdAndStepId(Integer id, String stepId);
}
