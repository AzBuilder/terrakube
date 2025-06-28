package io.terrakube.api;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;

import java.io.IOException;

public class EncryptionTests extends ServerApplicationTests {

    @Test
    void encryptSampleString() throws IOException {
        String encryptedValue = encryptionService.encrypt("1");
        System.out.println(encryptedValue);
        Assert.equals(encryptionService.decrypt(encryptedValue), "1");
    }


}
