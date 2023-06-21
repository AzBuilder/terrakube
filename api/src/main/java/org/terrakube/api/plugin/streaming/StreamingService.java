package org.terrakube.api.plugin.streaming;

import liquibase.repackaged.org.apache.commons.text.TextStringBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.terrakube.api.repository.StepRepository;
import org.terrakube.api.rs.job.step.Step;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class StreamingService {

    RedisTemplate redisTemplate;

    StepRepository stepRepository;

    public String getCurrentLogs(String stepId){
        Step step = stepRepository.getReferenceById(UUID.fromString(stepId));
        TextStringBuilder currentLogs = new TextStringBuilder();
        try {
            String idStream = String.valueOf(step.getJob().getId());
            List<MapRecord> streamData = redisTemplate.opsForStream().read(StreamOffset.fromStart(String.valueOf(step.getJob().getId())), StreamOffset.latest(String.valueOf(step.getJob().getId())));
            for (MapRecord mapRecord : streamData) {
                StringRecord stringRecord = StringRecord.of(mapRecord);
                String output = stringRecord.getValue().get("output");
                currentLogs.appendln(output);
            }
            log.info("Logs Size: {}", currentLogs.size());

        } catch (Exception ex ){
            log.error(ex.getMessage());

        }
        return currentLogs.toString();
    }
}
