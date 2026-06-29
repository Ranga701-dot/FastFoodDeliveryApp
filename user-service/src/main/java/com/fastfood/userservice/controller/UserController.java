package com.fastfood.userservice.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/profile")
    public String profile() {

        return "Welcome " +
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();
    }
}