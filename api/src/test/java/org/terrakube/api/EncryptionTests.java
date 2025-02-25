package org.terrakube.api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;

public class EncryptionTests extends ServerApplicationTests {

    @Test
    void encryptSampleString() throws IOException {
        System.out.println(internalEncryptionService.encrypt("1"));

        System.out.println(internalEncryptionService.decrypt("Lmwg3SuyVyUXigwyeL3cIw==:rsEz8g9+DTddJzPNdR5i0Q=="));
        System.out.println(internalEncryptionService.decrypt("50lo2k1YltIvX7AnysBS7g==:Jg5tfC0WKHSnhc3rehpwLQ=="));
        System.out.println(internalEncryptionService.decrypt("XzZvk27NLwq8T+3a6aPR5A==:iMMvmOwgpGF6GQ/5UKq9/g=="));
        System.out.println(internalEncryptionService.decrypt("XZ3lEvltygvOHu4XozmKGA==:wPjWuDwQLxT9yqZJQsxzEg=="));


        System.out.println(Base64.getUrlEncoder().encodeToString("gD0IWvMRbf74p93nMIlOBg==:c2a6xFBE7PXy1tNNjYU2JA==".getBytes()));

        System.out.println(Base64.getUrlDecoder().decode("Z0QwSVd2TVJiZjc0cDkzbk1JbE9CZz09OmMyYTZ4RkJFN1BYeTF0Tk5qWVUySkE9PQ==").toString());
    }


}
