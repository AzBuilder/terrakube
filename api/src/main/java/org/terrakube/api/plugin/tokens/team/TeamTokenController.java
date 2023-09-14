package org.terrakube.api.plugin.tokens.team;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.terrakube.api.rs.pat.Pat;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/access-token/v1/teams")
public class TeamTokenController {

    TeamTokenService teamTokenService;

    @GetMapping
    public ResponseEntity<List<Pat>> SearchTeams(Principal principal){
        JwtAuthenticationToken principalJwt = ((JwtAuthenticationToken) principal);
        Object objecd = principalJwt.getTokenAttributes().get("groups");
        log.info(objecd.getClass().toString());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
