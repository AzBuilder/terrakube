package org.azbuilder.api.rs.hooks.job;

import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.dsl.FlowConfig;
import org.azbuilder.api.repository.JobRepository;
import org.azbuilder.api.repository.StepRepository;
import org.azbuilder.api.rs.job.Command;
import org.azbuilder.api.rs.job.Job;
import org.azbuilder.api.rs.job.JobStatus;
import org.azbuilder.api.rs.job.step.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.Base64;
import java.util.Optional;

@Slf4j
public class JobCreateTclHook implements LifeCycleHook<Job> {

    @Autowired
    StepRepository stepRepository;

    @Autowired
    JobRepository jobRepository;

    @Override
    public void execute(LifeCycleHookBinding.Operation operation, LifeCycleHookBinding.TransactionPhase transactionPhase, Job job, RequestScope requestScope, Optional<ChangeSpec> optional) {
        if(job.getCommand().equals(Command.custom) && job.getStep().isEmpty()){
            Yaml yaml = new Yaml(new Constructor(FlowConfig.class));
            FlowConfig tclConfiguration = yaml.load(new String(Base64.getDecoder().decode(job.getTcl())));
            log.info(tclConfiguration.toString());
            tclConfiguration.getFlow().parallelStream().forEach(command ->{
                Step step = new Step();
                step.setId(command.getStep());
                step.setStatus(JobStatus.pending);
                step.setJob(jobRepository.getOne(job.getId()));
                stepRepository.save(step);
            });
        }
    }
}
