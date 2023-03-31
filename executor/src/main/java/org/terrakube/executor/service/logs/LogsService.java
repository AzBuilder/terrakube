package org.terrakube.executor.service.logs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.TextStringBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class LogsService implements ProcessLogs {

    LogsRepository logsRepository;

    @Override
    synchronized public void sendLogs(Integer jobId, String stepId, String output) {
        Optional<Logs> currentLogs = logsRepository.findById(stepId);
        if (!currentLogs.isPresent()) {
            currentLogs = Optional.of(
                    Logs.builder()
                            .id(stepId)
                            .jobId(jobId)
                            .output(output)
                            .ttl(2700L) //45 minutes
                            .build()
            );
        }
        currentLogs.get()
                .setOutput(
                        new TextStringBuilder(currentLogs.get().getOutput())
                                .appendln(output)
                                .toString()
                );
        logsRepository.save(currentLogs.get());
    }

    public void deleteLogs(String stepId) {
        Logs logs = logsRepository.findById(stepId).get();
        if (logs == null) {
            log.info("The key is not inside redis, I have no idea why :(");
        } else {
            log.warn("Deleting logs from redis with id {}", stepId);
            logsRepository.delete(logs);
        }
    }
}
