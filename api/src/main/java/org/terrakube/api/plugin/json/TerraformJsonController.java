package org.terrakube.api.plugin.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/terraform")
public class TerraformJsonController {

    @GetMapping("/index.json")
    public ResponseEntity<String> createToken() {
        WebClient client = WebClient.builder()
                .baseUrl("https://releases.hashicorp.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String terraformIndex = client.get()
                .uri("/terraform/index.json")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .block();
        return new ResponseEntity<>(terraformIndex, HttpStatus.OK);
    }
}


