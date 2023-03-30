package org.terrakube.api.plugin.streaming;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("logs")
@Getter
@Setter
public class Logs {

    private int id;

    private String stepId;
    private String output;
}
