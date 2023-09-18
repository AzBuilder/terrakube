package org.terrakube.api.plugin.token.pat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.terrakube.api.repository.PatRepository;
import org.terrakube.api.rs.token.pat.Pat;

import javax.crypto.SecretKey;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PatService {

    @Value("${org.terrakube.token.pat}")
    private String base64Key;
    private static final String ISSUER = "Terrakube";

    @Autowired
    private PatRepository patRepository;

    public String createToken(int days, String description, Object name, Object email, Object groups) {

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(this.base64Key));
        UUID keyId = UUID.randomUUID();

        log.info("Generated Pat {}", keyId);

        String jws = Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(String.format("%s (Token)", name))
                .setAudience(ISSUER)
                .setId(keyId.toString())
                .claim("email", email)
                .claim("email_verified", true)
                .claim("name", String.format("%s (Token)", name))
                .claim("groups", groups)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(days, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();

        Pat pat = new Pat();
        pat.setId(keyId);
        pat.setDays(days);
        pat.setDescription(description);

        patRepository.save(pat);

        return jws;
    }


    public List<Pat> searchToken(Principal principal) {
        JwtAuthenticationToken principalJwt = ((JwtAuthenticationToken) principal);
        return patRepository.findByCreatedBy((String) principalJwt.getTokenAttributes().get("email"));
    }
}
