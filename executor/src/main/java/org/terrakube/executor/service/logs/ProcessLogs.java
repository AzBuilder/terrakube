package org.terrakube.executor.service.logs;

public interface ProcessLogs {
    public void sendLogs(int id, String stepId, String output);
}
