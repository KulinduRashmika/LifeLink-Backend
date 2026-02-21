package com.lifelink.hospital_registration.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Hospital Registration Backend is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}