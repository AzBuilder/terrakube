package org.terrakube.api.plugin.streaming;

import liquibase.repackaged.org.apache.commons.text.TextStringBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class StreamingService {

    RedisTemplate redisTemplate;

    public String getCurrentLogs(String stepId){

        TextStringBuilder currentLogs = new TextStringBuilder();
        try {
            List<MapRecord> streamData = redisTemplate.opsForStream().read(Consumer.from("UI", String.valueOf(stepId)),
                    StreamReadOptions.empty().noack(),
                    StreamOffset.fromStart(stepId));
            
            for (MapRecord mapRecord : streamData) {
                Map<String, String> stream = (Map<String, String>) mapRecord.getValue();
                String output = stream.get("output");
                currentLogs.appendln(output);
            }
            log.info("Logs Size: {}", currentLogs.size());

        } catch (Exception ex ){
            log.error(ex.getMessage());

        }
        return currentLogs.toString();
    }
}
