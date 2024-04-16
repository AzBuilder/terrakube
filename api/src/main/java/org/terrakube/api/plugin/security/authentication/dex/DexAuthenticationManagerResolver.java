package org.terrakube.api.plugin.security.authentication.dex;

import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.io.Decoders;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.terrakube.api.repository.PatRepository;
import org.terrakube.api.repository.TeamTokenRepository;
import org.terrakube.api.rs.token.group.Group;
import org.terrakube.api.rs.token.pat.Pat;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;
import java.util.UUID;

@Builder
@Getter
@Setter
@Slf4j
public class DexAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private static final String jwtTypePat = "Terrakube";
    private static final String jwtTypeInternal = "TerrakubeInternal";
    private String dexIssuerUri;
    private String dexInternalIssuerUri;
    private String patJwtSecret;
    private String internalJwtSecret;
    private PatRepository patRepository;
    private TeamTokenRepository teamTokenRepository;

    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        ProviderManager providerManager = null;
        String issuer = "";
        try {
            issuer = getIssuer(request);
            if (isTokenDeleted(getTokenId(request))) {
                //FORCE TOKEN TO USE INTERNAL AUTH SO IT CAN ALWAYS FAIL
                issuer = jwtTypeInternal;
            }
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        switch (issuer) {
            case jwtTypePat:
                log.debug("Using Terrakube Authentication Provider");
                providerManager = new ProviderManager(new JwtAuthenticationProvider(getJwtEncoder(jwtTypePat)));
                break;
            case jwtTypeInternal:
                log.debug("Using Terrakube Internal Authentication Provider");
                providerManager = new ProviderManager(new JwtAuthenticationProvider(getJwtEncoder(jwtTypeInternal)));
                break;
            default:
                log.debug("Using Dex JWT Authentication Provider");
                if (dexInternalIssuerUri != null && !dexInternalIssuerUri.isEmpty()) {
                    // Using dex internal URI to authenticate
                    log.info("new internal communication {} {}", dexIssuerUri, dexInternalIssuerUri);
                    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                            .withIssuerLocation(this.dexIssuerUri)
                            .build();
                    jwtDecoder.setJwtValidator(
                            JwtValidators.createDefaultWithIssuer(this.dexInternalIssuerUri)
                    );
                    providerManager = new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
                } else {
                    // Using dex external URI to authenticate
                    providerManager = new ProviderManager(new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(this.dexIssuerUri)));
                }

                break;
        }
        return providerManager;
    }

    private JwtDecoder getJwtEncoder(String issuerType) {
        String jwtSecret = (issuerType.equals(jwtTypePat) ? patJwtSecret : internalJwtSecret);
        SecretKey jwtSecretKey = new SecretKeySpec(Decoders.BASE64URL.decode(jwtSecret), "HMACSHA256");
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private String getIssuer(HttpServletRequest request) {
        String token = request.getHeader("authorization").replace("Bearer ", "");
        String withoutSignature = token.substring(0, token.lastIndexOf('.') + 1);
        Jwt<Header, Claims> untrusted = Jwts.parserBuilder().build().parseClaimsJwt(withoutSignature);
        log.debug("Token {}", token);
        log.debug("Token Without Signature {}", withoutSignature);
        log.debug("Issuer {}", untrusted.getBody().getIssuer());

        return untrusted.getBody().getIssuer();
    }

    private String getTokenId(HttpServletRequest request) {
        String searchToken = request.getHeader("authorization").replace("Bearer ", "");
        String withoutSignature = searchToken.substring(0, searchToken.lastIndexOf('.') + 1);
        Jwt<Header, Claims> untrusted = Jwts.parserBuilder().build().parseClaimsJwt(withoutSignature);
        log.debug("TokenId {}", untrusted.getBody().getId());

        return untrusted.getBody().getId();
    }

    private boolean isTokenDeleted(String tokenId) {
        if (tokenId != null) {
            Optional<Pat> searchPat = patRepository.findById(UUID.fromString(tokenId));
            Optional<Group> searchGroupToken = teamTokenRepository.findById(UUID.fromString(tokenId));
            if (searchPat.isPresent()) {
                Pat pat = searchPat.get();
                if (pat.isDeleted()) {
                    return true;
                } else return false;
            }

            if (searchGroupToken.isPresent()) {
                Group group = searchGroupToken.get();
                if (group.isDeleted()) {
                    return true;
                } else return false;
            }
        }

        return false;
    }
}