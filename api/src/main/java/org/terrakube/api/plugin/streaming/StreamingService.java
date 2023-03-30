package org.terrakube.api.plugin.streaming;

import org.springframework.stereotype.Service;

@Service
public class StreamingService {

    LogsRepository logsRepository;
    public String getCurrentLogs(int id, String stepId){
        return logsRepository.getBydIAndStepId(id, stepId).getOutput();
    }
}
