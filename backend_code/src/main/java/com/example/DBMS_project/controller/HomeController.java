package com.example.DBMS_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Note: Use @Controller, NOT @RestController
public class HomeController {

    public String showHomePage() {
        // This returns the name of the file: "index.html"
        // Thymeleaf looks in src/main/resources/templates/ automatically
        return "index";
    }
}
