package io.terrakube.api.plugin.scheduler.module;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.terrakube.api.repository.OrganizationRepository;
import io.terrakube.api.rs.module.GitTagsCache;

@AllArgsConstructor
@Component
@Slf4j
public class CacheJob implements Job {

    OrganizationRepository organizationRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        GitTagsCache gitTagsCache = new GitTagsCache();
        organizationRepository.findAll().forEach(organization -> {
            organization.getModule().forEach(module -> {
                try {
                    gitTagsCache.setVersions(module.getRegistryPath(null), gitTagsCache.getVersionFromRepository(module.getSource(), module.getTagPrefix(), module.getVcs(), module.getSsh(), module.getGitHubAppToken()));
                } catch (Exception ex) {
                    log.error("Updating module index for {}", module.getRegistryPath(null));
                    log.error(ex.getMessage());
                }

            });
        });
    }
}
