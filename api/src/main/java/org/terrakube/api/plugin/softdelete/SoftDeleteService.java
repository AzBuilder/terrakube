package org.terrakube.api.plugin.softdelete;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terrakube.api.plugin.scheduler.ScheduleJobService;
import org.terrakube.api.repository.ScheduleRepository;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.workspace.Workspace;
import org.terrakube.api.rs.workspace.schedule.Schedule;

import java.text.ParseException;

@AllArgsConstructor
@Slf4j
@Service
public class SoftDeleteService {

    ScheduleJobService scheduleJobService;

    ScheduleRepository scheduleRepository;

    @Transactional
    public void deleteWorkspace(Workspace workspace){
        for(Schedule schedule: workspace.getSchedule()){
            try {
                scheduleJobService.deleteJobTrigger(schedule.getId().toString());
                schedule.setEnabled(false);
                scheduleRepository.save(schedule);
            } catch (ParseException | SchedulerException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void disableOrganization(Organization organization){

    }
}
