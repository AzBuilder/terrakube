package org.terrakube.executor.service.logs;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.TextStringBuilder;

import java.util.function.Consumer;

@Builder
@Slf4j
public class LogsConsumer implements Consumer<String> {

    @NonNull
    private Integer jobId;

    @NonNull
    private String stepId;
    @NonNull
    TextStringBuilder terraformOutput;
    @NonNull
    ProcessLogs processLogs;

    @Override
    public void accept(String logs) {
        log.info(logs);
        terraformOutput.appendln(logs);
        processLogs.sendLogs(jobId, stepId, logs);
    }
}
