package org.terrakube.api.plugin.scheduler.startup;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.repository.OrganizationRepository;

@AllArgsConstructor
@Component
@Slf4j
public class ModuleStartupJob implements Job {

    OrganizationRepository organizationRepository;

    @Transactional
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        organizationRepository.findAll().parallelStream().forEach(organization -> {
            organization.getModule().forEach(module -> {
                log.info("Refresh Module Index {}/{}/{} ", organization.getName(), module.getProvider(), module.getName());
            });
        });
    }
}
