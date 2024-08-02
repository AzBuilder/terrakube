package org.terrakube.api.rs.hooks.workspace;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.softdelete.SoftDeleteService;
import org.terrakube.api.plugin.vcs.WebhookService;
import org.terrakube.api.repository.GlobalVarRepository;
import org.terrakube.api.repository.WebhookRepository;
import org.terrakube.api.rs.globalvar.Globalvar;
import org.terrakube.api.rs.webhook.Webhook;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.rs.workspace.parameters.Category;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class WorkspaceManageHook implements LifeCycleHook<Workspace> {

    SoftDeleteService softDeleteService;
    WebhookService webhookService;
    WebhookRepository webhookRepository;
    GlobalVarRepository globalVarRepository;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation,
            LifeCycleHookBinding.TransactionPhase transactionPhase, Workspace workspace, RequestScope requestScope,
            Optional<ChangeSpec> optional) {
        log.info("WorkspaceManageHook {}", workspace.getId());
        switch (operation) {
            case UPDATE:
                if (workspace.isDeleted()) {
                    softDeleteService.disableWorkspaceSchedules(workspace);
                }

                if (workspace.getDefaultTemplate() != null && workspace.getDefaultTemplate().length() > 0) {
                    Optional<Webhook> searchWebHook = webhookRepository.findByReferenceId(workspace.getId().toString());
                    if (searchWebHook.isPresent()) {
                        Webhook webhook = searchWebHook.get();
                        log.warn("Updating default webhook template");
                        String templateMapping = "{\"push\":\"" + workspace.getDefaultTemplate() + "\"}";
                        webhook.setTemplateMapping(templateMapping);
                        webhookRepository.save(webhook);
                    }
                }
                break;
            case CREATE:
                switch (transactionPhase) {
                    case PRECOMMIT:
                        if (workspace.getExecutionMode() == null || workspace.getExecutionMode().isEmpty()) {
                            log.debug("setting default execution mode");
                            workspace.setExecutionMode(workspace.getOrganization().getExecutionMode());
                        }
                        break;

                    case POSTCOMMIT:
                        if(globalVarRepository.getGlobalvarByOrganizationAndCategoryAndKey(workspace.getOrganization(), Category.ENV, "TERRAKUBE_DISABLE_WEBHOOK") == null) {
                            log.info("Webhook support is enabled");
                            webhookService.createWorkspaceWebhook(workspace);
                        } else {
                            log.warn("Webhook support is disabled");
                        }
                        break;

                    default:
                        break;
                }

            default:
                log.info("No hook define for this operation");
                break;
        }
    }
}
