package org.terrakube.executor.service.logs;

import org.apache.commons.text.TextStringBuilder;
import org.springframework.stereotype.Service;

@Service
public class LogsService implements ProcessLogs {

    LogsRepository logsRepository;

    @Override
    synchronized public void sendLogs(int id, String stepId, String output) {
        Logs logs = logsRepository.getBydIdAAndStepId(id, stepId);
        if (logs == null) {
            logs = new Logs();
            logs.setId(id);
            logs.setStepId(stepId);
        }
        logs.setOutput(new TextStringBuilder(logs.getOutput()).appendln(output).toString());
        logsRepository.save(logs);
    }

    public void deleteLogs(int id, String stepId){
        Logs logs = logsRepository.getBydIdAAndStepId(id, stepId);
        logsRepository.delete(logs);
    }
}
