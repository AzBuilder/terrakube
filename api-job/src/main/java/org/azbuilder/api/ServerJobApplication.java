package org.azbuilder.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @deprecated The logic was migrated to api-server using quartz for high availability
 */
@Deprecated
@SpringBootApplication
@EnableScheduling
public class ServerJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerJobApplication.class, args);
    }

}
