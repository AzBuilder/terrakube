package org.terrakube.api.plugin.scheduler.job.tcl.executor.ephemeral;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.EnvFromSource;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.SecretEnvSource;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.scheduler.job.tcl.executor.ExecutorContext;
import org.terrakube.api.rs.job.Job;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EphemeralExecutorService {

    KubernetesClient kubernetesClient;
    EphemeralConfiguration ephemeralConfiguration;

    public boolean sendToEphemeralExecutor(Job job, ExecutorContext executorContext) {
        final String jobName = "job-" + job.getId();
        deleteEphemeralJob(job);
        log.info("Ephemeral Executor Image {}, Job: {}, Namespace: {}", ephemeralConfiguration.getImage(), jobName, ephemeralConfiguration.getNamespace());
        SecretEnvSource secretEnvSource = new SecretEnvSource();
        secretEnvSource.setName(ephemeralConfiguration.getSecret());
        EnvFromSource envFromSource = new EnvFromSource();
        envFromSource.setSecretRef(secretEnvSource);
        final List<EnvFromSource> executorEnvVarFromSecret = Arrays.asList(envFromSource);

        EnvVar executorFlagBatch = new EnvVar();
        executorFlagBatch.setName("EphemeralFlagBatch");
        executorFlagBatch.setValue("true");

        EnvVar executorFlagBatchJsonContent = new EnvVar();
        try {
            executorFlagBatchJsonContent.setName("EphemeralJobData");
            ObjectMapper mapper = new ObjectMapper();
            String jobJson = mapper.writeValueAsString(executorContext);
            executorFlagBatchJsonContent.setValue(Base64.getEncoder().encodeToString(jobJson.getBytes("UTF-8")));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        final List<EnvVar> executorEnvVarFlags = Arrays.asList(executorFlagBatch, executorFlagBatchJsonContent);


        io.fabric8.kubernetes.api.model.batch.v1.Job k8sJob = new JobBuilder()
                .withApiVersion("batch/v1")
                .withNewMetadata()
                .withName(jobName)
                .withNamespace(ephemeralConfiguration.getNamespace())
                .withLabels(Collections.singletonMap("jobId", executorContext.getJobId()))
                .withLabels(Collections.singletonMap("organizationId", executorContext.getOrganizationId()))
                .withLabels(Collections.singletonMap("workspaceId", executorContext.getWorkspaceId()))
                .endMetadata()
                .withNewSpec()
                .withNewTemplate()
                .withNewSpec()
                .addNewContainer()
                .withName(jobName)
                .withEnvFrom(executorEnvVarFromSecret)
                .withImage(ephemeralConfiguration.getImage())
                .withEnv(executorEnvVarFlags)
                .endContainer()
                .withRestartPolicy("Never")
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        log.info("Running ephemeral job");
        kubernetesClient.batch().v1().jobs().inNamespace(ephemeralConfiguration.getNamespace()).createOrReplace(k8sJob);
        return true;
    }

    public void deleteEphemeralJob(Job job){
        try {
            io.fabric8.kubernetes.api.model.batch.v1.Job jobK8s = kubernetesClient
                    .batch()
                    .jobs()
                    .inNamespace(ephemeralConfiguration.getNamespace())
                    .withName("job-" + job.getId())
                    .get();

            if (jobK8s != null) {
                kubernetesClient
                        .batch()
                        .jobs()
                        .inNamespace(ephemeralConfiguration.getNamespace())
                        .withName("job-" + job.getId())
                        .delete();
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
