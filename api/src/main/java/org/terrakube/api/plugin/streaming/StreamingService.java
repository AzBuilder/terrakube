package org.terrakube.api.plugin.streaming;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class StreamingService {

    LogsRepository logsRepository;
    public String getCurrentLogs(String stepId){
        

        Optional<Logs> logs = logsRepository.findById(stepId);
        log.info("Current {} {}", stepId, logs.isPresent()? logs.get().getOutput() : null);

        return (logs.isPresent()? logs.get().getOutput() : null);
    }
}
