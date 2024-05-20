package org.terrakube.api.rs.job;

public enum JobStatus {
    pending,
    waitingApproval,
    approved,
    queue,
    running,
    completed,
    noChanges,
    rejected,
    cancelled,
    failed,
    unknown
}
