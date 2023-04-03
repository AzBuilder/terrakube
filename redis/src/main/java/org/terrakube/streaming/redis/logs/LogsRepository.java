package org.terrakube.streaming.redis.logs;

import org.springframework.data.repository.CrudRepository;

public interface LogsRepository extends CrudRepository<Logs, String> {

}
