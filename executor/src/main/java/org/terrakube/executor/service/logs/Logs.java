package org.terrakube.executor.service.logs;

import lombok.Setter;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("logs")
@Getter
@Setter
public class Logs {

    private int id;
    private String stepId;
    private String output;
}
