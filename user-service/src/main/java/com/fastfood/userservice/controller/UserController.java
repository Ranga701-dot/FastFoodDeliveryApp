package com.fastfood.userservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerProfile() {
        return "Welcome Customer";
    }

    @GetMapping("/restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public String restaurantOwner() {
        return "Welcome Restaurant Owner";
    }

    @GetMapping("/delivery")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public String deliveryPartner() {
        return "Welcome Delivery Partner";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Welcome Admin";
    }
}