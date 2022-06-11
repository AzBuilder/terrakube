package org.azbuilder.api.rs.job;

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
