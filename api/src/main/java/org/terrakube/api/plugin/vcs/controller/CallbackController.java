package org.terrakube.api.plugin.vcs.controller;

import lombok.extern.slf4j.Slf4j;
import org.terrakube.api.plugin.vcs.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/callback/v1")
public class CallbackController {

    @Autowired
    TokenService tokenService;

    @GetMapping("/vcs/{vcsId}")
    public ResponseEntity<String> connected(@PathVariable("vcsId") String vcsId, @RequestParam String code){
        if(code != null){
            log.info("Updating connection for vcs {}", vcsId);
            tokenService.generateAccessToken(vcsId, code);
            return ResponseEntity.ok("Connected");
        }
        return ResponseEntity.ok().build();
    }

}

