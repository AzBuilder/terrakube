package org.terrakube.api.plugin.scheduler.job.tcl.executor.ephemeral;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@ConditionalOnMissingBean(KubernetesClient.class)
public class K8sClientAutoConfiguration {

    @Bean
    public KubernetesClient kubernetesClient(EphemeralConfiguration ephemeralConfiguration) {
        log.info("Ephemeral Executor Configuration Image {}, Namespace: {}, NodeSelector: {}", ephemeralConfiguration.getImage(), ephemeralConfiguration.getNamespace(), ephemeralConfiguration.getNodeSelector());
        return new DefaultKubernetesClient();
    }
}
