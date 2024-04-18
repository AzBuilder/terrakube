package org.terrakube.api.plugin.token.dynamic;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/.well-known/jwks")
public class JwksController {

    private JwkData jwkData;
    public static final String kid = "03446895-220d-47e1-9564-4eeaa3691b42";

    @Value("${org.terrakube.dynamic.credentials.public-key-path}")
    String publicKeyPath;

    @GetMapping(produces = "application/json")
    public ResponseEntity<JwkData> jwksEndpoint() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        if (jwkData == null) {
            jwkData = getJwkData();
        }
        return ResponseEntity.of(Optional.of(jwkData));

    }

    private JwkData getJwkData() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        String publicKeyPEM = FileUtils.readFileToString(new File(publicKeyPath), StandardCharsets.UTF_8);

        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");

        String publicKeyPEMFinal = "";
        String line;
        BufferedReader bufReader = new BufferedReader(new StringReader(publicKeyPEM));
        while( (line=bufReader.readLine()) != null )
        {
            publicKeyPEMFinal += line;
        }

        log.info("Dynamic Credentials Public Key: {}", publicKeyPEMFinal);

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEMFinal);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);

        RSAPublicKey rsa = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        String exponent = Base64.getUrlEncoder().encodeToString(rsa.getPublicExponent().toByteArray());
        String modulus = Base64.getUrlEncoder().encodeToString(rsa.getModulus().toByteArray());
        log.info("RSA Exponent: {}", exponent);
        log.info("RSA Modulus: {}", modulus);

        JwkData jwkData = new JwkData();
        jwkData.setKeys(new ArrayList());

        JwkElement jwkElement = new JwkElement();
        jwkElement.setKty("RSA");
        jwkElement.setN(modulus);
        jwkElement.setE(exponent);
        jwkElement.setKid(kid);
        jwkElement.setUse("sig");
        jwkElement.setAlg("RS256");

        jwkData.getKeys().add(jwkElement);

        return  jwkData;
    }
}

@Getter
@Setter
class JwkData {
    List<JwkElement> keys;
}

@Getter
@Setter
class JwkElement {

    private String kty;
    private String use;
    private String n;
    private String e;
    private String kid;
    private String alg;
}
