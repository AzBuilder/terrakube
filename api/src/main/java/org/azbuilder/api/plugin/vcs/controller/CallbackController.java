package org.azbuilder.api.plugin.vcs.controller;

import lombok.extern.slf4j.Slf4j;
import org.azbuilder.api.plugin.vcs.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/callback/v1")
public class CallbackController {

    @Autowired
    TokenService tokenService;

    @GetMapping("/vcs/{vcsId}")
    public String connected(@PathVariable("vcsId") String vcsId, @RequestParam String code){
        log.info("Updating connection for vcs {}", vcsId);
        tokenService.generateAccessToken(vcsId,code);
        return "Connected";
    }

}

