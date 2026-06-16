package com.kwsni.caught_up.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiteController {
    @GetMapping("/about")
    public String showAbout() {
        return "about";
    }

    @GetMapping("/legal")
    public String showLegal() {
        return "legal";
    }
}
