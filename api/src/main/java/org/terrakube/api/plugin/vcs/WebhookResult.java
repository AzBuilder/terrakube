package org.terrakube.api.plugin.vcs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookResult {
    private String branch;
    private boolean isValid;
    private String event;
    private String createdBy;
    private String via;
}
