package org.terrakube.api.plugin.json;

import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

@AllArgsConstructor
@RestController
@RequestMapping("/terraform")
public class TerraformJsonController {

    TerraformJsonProperties terraformJsonProperties;

    @GetMapping(value= "/index.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createToken() throws IOException {
        String terraformIndex = "";
        if(terraformJsonProperties.getReleasesUrl() != null && !terraformJsonProperties.getReleasesUrl().isEmpty()) {
            terraformIndex = IOUtils.toString(URI.create(terraformJsonProperties.getReleasesUrl()), Charset.defaultCharset().toString());
        } else {
            terraformIndex = IOUtils.toString(URI.create("https://releases.hashicorp.com/terraform/index.json"), Charset.defaultCharset().toString());
        }
        return new ResponseEntity<>(terraformIndex, HttpStatus.OK);
    }
}


