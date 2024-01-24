package org.terrakube.api.plugin.json;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RestController
@RequestMapping("/tofu")
public class TofuJsonController {


    @GetMapping(value= "/index.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createToken() throws IOException {
        String tofuIndex = "";
        String defaultUrl="https://api.github.com/repos/opentofu/opentofu/releases";
        log.warn("Using tofu releases URL {}", defaultUrl);
        tofuIndex = IOUtils.toString(URI.create(defaultUrl), Charset.defaultCharset().toString());
        return new ResponseEntity<>(tofuIndex, HttpStatus.OK);
    }
}


