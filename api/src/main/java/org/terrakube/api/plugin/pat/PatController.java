package org.terrakube.api.plugin.pat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.rs.pat.Pat;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/pat/v1")
public class PatController {

    @Autowired
    PatService patService;

    @PostMapping
    public ResponseEntity<PatResponse> createToken(@RequestBody PatTokenRequest patTokenRequest, Principal principal) {
        PatResponse patResponse = new PatResponse();
        patResponse.setToken(patService.createToken(patTokenRequest.getDays(), patTokenRequest.getDescription(), principal));
        return new ResponseEntity<>(patResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Pat>> searchToken(Principal principal){
        return new ResponseEntity<>(patService.searchToken(principal), HttpStatus.ACCEPTED);
    }

    @Getter
    @Setter
    private class PatResponse {
        private String token;
    }

    @Getter
    @Setter
    public static class PatTokenRequest {

        private int days;
        private String description;
    }
}
