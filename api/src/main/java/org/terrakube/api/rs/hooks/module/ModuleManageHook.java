package org.terrakube.api.rs.hooks.module;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.terrakube.api.plugin.scheduler.module.DeleteStorageCacheJob;
import org.terrakube.api.plugin.vcs.provider.github.GitHubTokenService;
import org.terrakube.api.rs.module.Module;
import org.terrakube.api.rs.vcs.GitHubAppToken;
import org.terrakube.api.rs.vcs.VcsConnectionType;
import org.terrakube.api.rs.vcs.VcsType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class ModuleManageHook implements LifeCycleHook<Module> {

    private static final String PREFIX_JOB_MODULE_DELETE_STORAGE = "TerrakubeV2_ModuleDeleteStorage";

    Scheduler scheduler;
    GitHubTokenService gitHubTokenService;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation,
            LifeCycleHookBinding.TransactionPhase transactionPhase, Module module, RequestScope requestScope,
            Optional<ChangeSpec> optional) {

        switch (operation) {
            case CREATE:
                log.info("ModuleManageHook creation hook for {}/{}/{}", module.getOrganization().getName(),
                        module.getName(), module.getProvider());
                switch (transactionPhase) {
                    case PRECOMMIT:
                        checkAndCreateGitHubAppToken(module);
                        break;
                    default:
                        break;
                }
                break;
            case DELETE:
                try {
                    log.warn("ModuleManageHook Delete Storage for {}/{}/{}", module.getOrganization().getName(),
                            module.getName(), module.getProvider());
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("moduleOrganization", module.getOrganization().getName());
                    jobDataMap.put("moduleName", module.getName());
                    jobDataMap.put("moduleProvider", module.getProvider());

                    JobDetail jobDetail = JobBuilder.newJob().ofType(DeleteStorageCacheJob.class)
                            .storeDurably()
                            .setJobData(jobDataMap)
                            .withIdentity(PREFIX_JOB_MODULE_DELETE_STORAGE + "_" + UUID.randomUUID())
                            .withDescription("ModuleDeleteStorage")
                            .build();

                    Trigger trigger = TriggerBuilder.newTrigger()
                            .startNow()
                            .forJob(jobDetail)
                            .withIdentity(PREFIX_JOB_MODULE_DELETE_STORAGE + "_" + UUID.randomUUID())
                            .withDescription("ModuleDeleteStorageV1")
                            .startNow()
                            .build();

                    log.info("Create Schedule to delete module cache form storage: {}", jobDetail.getKey());
                    scheduler.scheduleJob(jobDetail, trigger);
                } catch (SchedulerException e) {
                    log.error(e.getMessage());
                }
                break;
            default:
                log.warn("Hook not supported in module");
                break;
        }
    }

    private void checkAndCreateGitHubAppToken(Module module) {
        if (module.getVcs().getConnectionType() == VcsConnectionType.OAUTH
                || module.getVcs().getVcsType() != VcsType.GITHUB)
            return;
        String[] ownerAndRepo;
        GitHubAppToken gitHubAppToken = null;
        try {
            ownerAndRepo = Arrays
                    .copyOfRange(new URI(module.getSource()).getPath().replaceAll("\\.git$", "").split("/"), 1, 3);
            gitHubAppToken = gitHubTokenService.getGitHubAppToken(module.getVcs(), ownerAndRepo);
            log.debug("Successfully fetched GitHub App Token for module {}/{}/{}", module.getOrganization().getName(), module.getName(), module.getProvider());
        } catch (URISyntaxException | JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Failed to fetch GitHub App Token for module {}/{}/{}, error {}", module.getOrganization().getName(), module.getName(), module.getProvider(), e);
        }
        module.setGitHubAppToken(gitHubAppToken);
    }
}
