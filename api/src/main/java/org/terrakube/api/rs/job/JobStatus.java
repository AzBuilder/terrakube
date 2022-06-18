package org.terrakube.api.rs.job;

public enum JobStatus {
    pending,
    waitingApproval,
    approved,
    queue,
    running,
    completed,

    rejected,
    cancelled,
    failed
}
