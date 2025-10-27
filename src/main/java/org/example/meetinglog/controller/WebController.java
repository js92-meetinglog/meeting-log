package org.example.meetinglog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        log.debug("Home page requested, redirecting to login");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        log.debug("Login page requested");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        log.debug("Dashboard page requested");
        return "dashboard";
    }
}