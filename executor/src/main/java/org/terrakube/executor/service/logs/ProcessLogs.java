package org.terrakube.executor.service.logs;

public interface ProcessLogs {
    public void sendLogs(Integer jobId, String stepId, int lineNumber, String output);
}
