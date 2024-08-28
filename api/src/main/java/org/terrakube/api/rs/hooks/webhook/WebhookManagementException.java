package org.terrakube.api.rs.hooks.webhook;

import com.yahoo.elide.core.exceptions.HttpStatusException;


public class WebhookManagementException extends HttpStatusException {
    protected WebhookManagementException(int failedDependency, String message) {
        super(failedDependency, message);
    }
}
