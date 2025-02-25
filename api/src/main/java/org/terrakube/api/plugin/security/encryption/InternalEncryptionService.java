package org.terrakube.api.plugin.security.encryption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
public class InternalEncryptionService {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @Value("${org.terrakube.token.internal}")
    private String internalToken;

    /**
     * Encrypts the given value using AES encryption with the internal token as the key.
     *
     * @param value The plaintext string to encrypt
     * @return The encrypted string in Base64 format, with IV prepended
     */
    public String encrypt(String value) {
        try {
            // Generate SecretKeySpec using the internal token
            SecretKeySpec keySpec = new SecretKeySpec(generateHashFromToken(internalToken), "AES");

            // Generate a random Initialization Vector (IV)
            byte[] iv = generateIv();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Configure the Cipher for encryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // Perform encryption
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            // Encode IV and encrypted data in Base64, and return as "IV:EncryptedData"
            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
            return ivBase64 + ":" + encryptedBase64;
        } catch (Exception e) {
            log.error("Error during AES encryption: {}", e.getMessage(), e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts the given encrypted text (Base64 encoded) using AES with the internal token as the key.
     *
     * @param encryptedText The encrypted string in the format "IV:EncryptedData"
     * @return The decrypted plaintext string
     */
    public String decrypt(String encryptedText) {
        try {
            // Split the input into IV and cipher text
            String[] parts = encryptedText.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid encrypted string format. Expected 'IV:EncryptedData'");
            }
            String ivBase64 = parts[0];
            String encryptedBase64 = parts[1];

            // Decode IV and encrypted data from Base64
            byte[] iv = Base64.getDecoder().decode(ivBase64);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

            // Create SecretKeySpec and IvParameterSpec for decryption
            SecretKeySpec keySpec = new SecretKeySpec(generateHashFromToken(internalToken), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Configure Cipher for decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // Perform decryption
            byte[] originalBytes = cipher.doFinal(encryptedBytes);

            // Return the plaintext string
            return new String(originalBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error during AES decryption: {}", e.getMessage(), e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Generates a 256-bit hash from the provided token using SHA-256,
     * which will be used as the AES key.
     *
     * @param token The secret token used as input for the hash
     * @return A 256-bit hash byte array
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is not available
     */
    private byte[] generateHashFromToken(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(token.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a random 16-byte Initialization Vector (IV) for AES.
     *
     * @return A randomly generated IV byte array
     */
    private byte[] generateIv() {
        byte[] iv = new byte[16]; // AES block size is 16 bytes
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}