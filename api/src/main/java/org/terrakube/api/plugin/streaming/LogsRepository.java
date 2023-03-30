package org.terrakube.api.plugin.streaming;

import org.springframework.data.repository.CrudRepository;

public interface LogsRepository extends CrudRepository<Logs, Integer> {

    Logs getBydIAndStepId(Integer id, String stepId);
}
