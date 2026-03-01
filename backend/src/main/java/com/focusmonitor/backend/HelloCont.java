package com.focusmonitor.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class HelloCont {
    @GetMapping
    public String hello() {
        return "Hello, Focus Monitor!";
    }
}
