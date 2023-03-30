package org.terrakube.executor.service.logs;

import lombok.Setter;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("logs")
@Getter
@Setter
public class Logs {
    
    @Id
    private String id;
    private String output;
}
