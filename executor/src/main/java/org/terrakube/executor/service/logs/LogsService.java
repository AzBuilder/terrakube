package org.terrakube.executor.service.logs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.TextStringBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class LogsService implements ProcessLogs {

    LogsRepository logsRepository;

    @Override
    synchronized public void sendLogs(Integer id, String stepId, String output) {
        Logs logs = null;
        if (!logsRepository.findById(stepId).isPresent()) {
            logs = new Logs();
            logs.setId(stepId);
        }else{
            logs = logsRepository.findById(stepId).get();
        }
        logs.setOutput(new TextStringBuilder(logs.getOutput()).appendln(output).toString());
        logsRepository.save(logs);
    }

    public void deleteLogs(Integer id, String stepId){
        Logs logs = logsRepository.findById(stepId).get();
        for(Logs logs1: logsRepository.findAll()){
            log.info("Redis {}", logs1.getId());
        }
        if(logs == null){
            log.info("This is null, I have no idea why");
        }
        logsRepository.delete(logs);
    }
}
