package org.terrakube.api.plugin.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/keys/v1")
public class JwtController {

    @Value("${org.terrakube.token.pat}")
    private String base64Key;
    private static final String ISSUER = "Terrakube";

    @GetMapping("/generate")
    public ResponseEntity<InternalToken> generateToken(Principal principal) {

        JwtAuthenticationToken principalJwt = ((JwtAuthenticationToken) principal);
        log.info("{}", principalJwt);

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(this.base64Key));

        String jws = Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(String.format("%s (Token)", principalJwt.getTokenAttributes().get("name")))
                .setAudience(ISSUER)
                .claim("email", principalJwt.getTokenAttributes().get("email"))
                .claim("email_verified", true)
                .claim("name", String.format("%s (Token)", principalJwt.getTokenAttributes().get("name")))
                .claim("groups", principalJwt.getTokenAttributes().get("groups"))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();

        InternalToken internalToken = new InternalToken();
        internalToken.setToken(jws);
        return new ResponseEntity<>(internalToken, HttpStatus.ACCEPTED);
    }

    @Getter
    @Setter
    private class InternalToken {
        private String token;
    }
}
