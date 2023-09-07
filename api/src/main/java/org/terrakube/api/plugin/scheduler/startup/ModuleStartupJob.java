package org.terrakube.api.plugin.scheduler.startup;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.repository.OrganizationRepository;
import org.terrakube.api.rs.module.ModuleCache;

@AllArgsConstructor
@Component
@Slf4j
public class ModuleStartupJob implements Job {

    OrganizationRepository organizationRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ModuleCache moduleCache = new ModuleCache();
        organizationRepository.findAll().forEach(organization -> {
            organization.getModule().forEach(module -> {
                try {
                    moduleCache.setVersions(module.getRegistryPath(null), moduleCache.getVersionFromRepository(module.getSource(), module.getVcs(), module.getSsh()));
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }

            });
        });
    }
}
