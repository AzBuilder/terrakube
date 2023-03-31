package org.terrakube.api.plugin.streaming;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StreamingService {

    LogsRepository logsRepository;
    public String getCurrentLogs(String stepId){
        Optional<Logs> logs = logsRepository.findById(stepId);
        return (logs.isPresent()? logs.get().getOutput() : null);
    }
}
