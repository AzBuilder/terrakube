package org.terrakube.executor.service.logs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LogsService implements ProcessLogs {

    RedisTemplate redisTemplate;

    @Override
    public void sendLogs(Integer jobId, String stepId, int lineNumber, String output) {
        Map<String, String> streamData = new LinkedHashMap();
        streamData.put("jobId", String.valueOf(jobId));
        streamData.put("stepId", stepId);
        streamData.put("lineNumber", String.valueOf(lineNumber));
        streamData.put("output", output);

        StringRecord record = StreamRecords.string(streamData).withStreamKey(stepId);

        redisTemplate.opsForStream().add(record);
    }

    public void deleteLogs(String stepId) {
            redisTemplate.delete(stepId);
    }
}
