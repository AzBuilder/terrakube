package org.terrakube.api.plugin.json;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

@Slf4j
@RestController
@RequestMapping("/terraform")
public class TerraformJsonController {

    @GetMapping(value= "/index.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createToken() throws IOException {
        String terraformIndex = IOUtils.toString(URI.create("https://releases.hashicorp.com/terraform/index.json"), Charset.defaultCharset().toString());
        return new ResponseEntity<>(terraformIndex, HttpStatus.OK);
    }
}


