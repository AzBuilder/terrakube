package io.terrakube.api.plugin.json;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/tofu")
public class TofuJsonController {

    private static final String TOFU_REDIS_KEY = "tofuReleasesResponse";
    TofuJsonProperties tofuJsonProperties;
    RedisTemplate redisTemplate;

    @GetMapping(value= "/index.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTofuReleases() throws IOException {
        String tofuIndex = "";
        if(redisTemplate.hasKey(TOFU_REDIS_KEY)) {
            log.info("Getting tofu releases from redis....");
            String tofuRedis = (String) redisTemplate.opsForValue().get(TOFU_REDIS_KEY);
            return new ResponseEntity<>(tofuRedis, HttpStatus.OK);
        } else {
            log.info("Getting tofu releases from default endpoint....");
            if(tofuJsonProperties.getReleasesUrl() != null && !tofuJsonProperties.getReleasesUrl().isEmpty()) {
                log.info("Using tofu releases URL {}", tofuJsonProperties.getReleasesUrl());
                tofuIndex = IOUtils.toString(URI.create(tofuJsonProperties.getReleasesUrl()), Charset.defaultCharset().toString());
            } else {
                String defaultUrl="https://api.github.com/repos/opentofu/opentofu/releases";
                log.warn("Using tofu releases URL {}", defaultUrl);
                tofuIndex = IOUtils.toString(URI.create(defaultUrl), Charset.defaultCharset().toString());
            }
            log.warn("Saving tofu releases to redis...");
            redisTemplate.opsForValue().set(TOFU_REDIS_KEY, tofuIndex);
            redisTemplate.expire(TOFU_REDIS_KEY, 30, TimeUnit.MINUTES);
            return new ResponseEntity<>(tofuIndex, HttpStatus.OK);
        }

    }
}


