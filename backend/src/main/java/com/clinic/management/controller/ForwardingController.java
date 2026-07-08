package com.clinic.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardingController {

    // Match everything without a suffix (so we do not match static resources like .js, .css)
    @RequestMapping(value = "/**/{path:[^\\.]*}")
    public String redirect() {
        // Forward to home page so that route is preserved.
        return "forward:/index.html";
    }
}
