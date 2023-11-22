package org.terrakube.api.rs.hooks.workspace;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.softdelete.SoftDeleteService;
import org.terrakube.api.plugin.vcs.WebhookService;
import org.terrakube.api.rs.workspace.Workspace;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class WorkspaceManageHook implements LifeCycleHook<Workspace> {

    SoftDeleteService softDeleteService;
    WebhookService webhookService;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Workspace workspace, RequestScope requestScope, Optional<ChangeSpec> optional) {
        log.info("WorkspaceManageHook {}", workspace.getId());
        switch (operation){
            case UPDATE:
                if(workspace.isDeleted()){
                    softDeleteService.disableWorkspaceSchedules(workspace);
                }
                break;
            case CREATE:
                log.info(null);
                webhookService.createWorkspaceWebhook(workspace);
            default:
                log.info("No hook define for this operation");
                break;
        }
    }
}
