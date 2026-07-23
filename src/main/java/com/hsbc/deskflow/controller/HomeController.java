package com.hsbc.deskflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/app", "/book"})
    public String home() {
        return "forward:/index.html";
    }
}
