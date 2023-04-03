package org.terrakube.api.plugin.streaming;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.terrakube.streaming.redis.logs.Logs;
import org.terrakube.streaming.redis.logs.LogsRepository;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class StreamingService {

    LogsRepository logsRepository;
    public String getCurrentLogs(String stepId){
        

        Optional<Logs> logs = logsRepository.findById(stepId);
        log.info("Current {} {}", stepId, logs.isPresent()? logs.get().getOutput() : null);


        for(Logs logs1 : logsRepository.findAll()){
            log.info("{}",logs1.toString());
            if(logs1.getId().equals(stepId)){
                log.info("Id exists");
            } else {
                log.info("id not found");
            }
        }

        return (logs.isPresent()? logs.get().getOutput() : null);
    }
}
