package org.terrakube.executor.service.logs;

public interface ProcessLogs {
    public void sendLogs(Integer id, String stepId, String output);
}
