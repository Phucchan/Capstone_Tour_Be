package com.fpt.capstone.tourism.controller;


import com.fpt.capstone.tourism.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String test() {
        User user = new User();
        user.setFullName("John Doe");
        return "Test successful!";
    }
}
