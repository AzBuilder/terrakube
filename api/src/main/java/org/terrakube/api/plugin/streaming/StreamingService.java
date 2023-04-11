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

        TextStringBuilder logs = new TextStringBuilder();
        try {
            List<MapRecord> streamData = redisTemplate.opsForStream().read(StreamOffset.fromStart(stepId), StreamOffset.latest(stepId));

            for (MapRecord record : streamData) {
                StringRecord record1 = StringRecord.of(record);
                String output = record1.getValue().get("output");
                log.info("{}", output);
                logs.appendln(output);
            }
        } catch (Exception ex ){
            log.error(ex.getMessage());

        }
        return logs.toString();
    }
}
