package org.azbuilder.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Deprecated
@SpringBootApplication
@EnableScheduling
public class ServerJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerJobApplication.class, args);
    }

}
