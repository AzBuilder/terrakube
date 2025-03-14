package org.terrakube.api.plugin.vcs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class WebhookResult {
    private String workspaceId;
    private String branch;
    private boolean isValid;
    private String event;
    private String createdBy;
    private String via;
    private List<String> fileChanges;
    private String commit;
    private Number prNumber;
}
