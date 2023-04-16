package org.terrakube.api.plugin.streaming;

import liquibase.repackaged.org.apache.commons.text.TextStringBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class StreamingService {

    RedisTemplate redisTemplate;

    public String getCurrentLogs(String stepId){

        TextStringBuilder currentLogs = new TextStringBuilder();
        try {
            List<MapRecord> streamData = redisTemplate.opsForStream().read(StreamOffset.fromStart(stepId), StreamOffset.latest(stepId));
            for (MapRecord mapRecord : streamData) {
                StringRecord stringRecord = StringRecord.of(mapRecord);
                String output = stringRecord.getValue().get("output");
                currentLogs.appendln(output);
            }
            log.info("{}", currentLogs);
        } catch (Exception ex ){
            log.error(ex.getMessage());

        }
        return currentLogs.toString();
    }
}
