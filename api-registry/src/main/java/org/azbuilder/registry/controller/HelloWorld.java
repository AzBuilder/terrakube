package org.azbuilder.registry.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloWorld {

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> terraformJson() {
        return ResponseEntity.ok().body(null);
    }
}
