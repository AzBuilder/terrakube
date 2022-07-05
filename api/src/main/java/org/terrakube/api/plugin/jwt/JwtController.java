package org.terrakube.api.plugin.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/keys/v1")
public class JwtController {

    @GetMapping("/generate")
    public ResponseEntity<String> connected() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E="));

        String jws = Jwts.builder()
                .setIssuer("Stormpath")
                .setSubject("msilverman")
                .claim("name", "Micah Silverman")
                .claim("scope", "admins")

                // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
                // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
                .signWith(key)
                .compact();

        return new ResponseEntity<>(jws, HttpStatus.ACCEPTED);
    }
}
