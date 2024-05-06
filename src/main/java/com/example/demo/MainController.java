package com.example.demo;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Home")
@RestController
public class MainController {

    @GetMapping("/")
    public String index() {
        return "Welcome to the Who Buys Coffee App!";
    }
}
