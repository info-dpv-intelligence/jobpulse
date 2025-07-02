package com.jobpulse.job_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "pong!";
    }

    @GetMapping("/protected-ping")
    public String protectedPing() {
        return "protected pong!";
    }
}