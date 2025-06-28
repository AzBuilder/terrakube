package io.terrakube.api.plugin.vcs.controller;

import lombok.extern.slf4j.Slf4j;
import io.terrakube.api.plugin.vcs.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequestMapping("/callback/v1")
public class CallbackController {

    @Autowired
    TokenService tokenService;

    @GetMapping("/vcs/{vcsId}")
    public ResponseEntity<String> connected(@PathVariable("vcsId") String vcsId, @RequestParam String code) {
        if (code != null) {
            log.info("Updating connection for vcs {}", vcsId);
            String redirectUrl = tokenService.generateAccessToken(vcsId, code);

            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("location", redirectUrl);
                return new ResponseEntity<String>(headers, HttpStatus.FOUND);
            }
            return ResponseEntity.ok("Connected");

        }
        return ResponseEntity.ok().build();
    }

}
