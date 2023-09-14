package org.terrakube.api.plugin.tokens.team;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/access-token/v1/teams")
public class TeamTokenController {

    TeamTokenService teamTokenService;

    @PostMapping
    public ResponseEntity<TeamToken> createToken(@RequestBody GroupTokenRequest groupTokenRequest,  Principal principal) {
        TeamToken teamToken = teamTokenService.createTeamToken(groupTokenRequest.getGroup(), groupTokenRequest.getDays(), groupTokenRequest.getDescription(), ((JwtAuthenticationToken) principal), );
        return new ResponseEntity<>(teamToken, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CurrentGroupsResponse> SearchTeams(Principal principal){
        JwtAuthenticationToken principalJwt = ((JwtAuthenticationToken) principal);
        CurrentGroupsResponse groupList = new CurrentGroupsResponse();
        groupList.setGroups(new ArrayList());
        teamTokenService.getCurrentGroups(principalJwt).forEach(group->{
            groupList.getGroups().add(group);
        });
        return new ResponseEntity<>(groupList, HttpStatus.FOUND);
    }

    @Getter
    @Setter
    private class CurrentGroupsResponse {
        private List<String> groups;
    }

    @Getter
    @Setter
    public static class TeamToken {
        private String token;
    }

    @Getter
    @Setter
    private class GroupTokenRequest{
        private String group;
        private String description;
        private int days;
    }

}
