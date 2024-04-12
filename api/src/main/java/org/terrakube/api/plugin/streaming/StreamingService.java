package org.terrakube.api.plugin.streaming;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.terrakube.api.repository.StepRepository;
import org.terrakube.api.rs.job.JobStatus;
import org.terrakube.api.rs.job.step.Step;
import org.apache.commons.text.TextStringBuilder;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class StreamingService {

    RedisTemplate redisTemplate;

    StepRepository stepRepository;

    public String getCurrentLogs(String stepId){
        TextStringBuilder currentLogs = new TextStringBuilder();
        try {
            Step step = stepRepository.getReferenceById(UUID.fromString(stepId));
            if(!step.getStatus().equals(JobStatus.completed) && !step.getStatus().equals(JobStatus.failed)) {
                List<MapRecord> streamData = redisTemplate.opsForStream().read(StreamOffset.fromStart(String.valueOf(step.getJob().getId())), StreamOffset.latest(String.valueOf(step.getJob().getId())));
                for (MapRecord mapRecord : streamData) {
                    StringRecord stringRecord = StringRecord.of(mapRecord);
                    String output = stringRecord.getValue().get("output");
                    currentLogs.appendln(output);
                }
                log.info("Logs Size: {}", currentLogs.size());
            }
        } catch (Exception ex ){
            log.error(ex.getMessage());

        }
        return currentLogs.toString();
    }
}
