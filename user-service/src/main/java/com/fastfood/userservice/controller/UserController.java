package com.fastfood.userservice.controller;

import com.fastfood.userservice.dto.MessageResponse;
import com.fastfood.userservice.dto.UpdateProfileRequest;
import com.fastfood.userservice.dto.UpdateRoleRequest;
import com.fastfood.userservice.dto.UserProfileResponse;
import com.fastfood.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
//    @GetMapping("/profile")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public String customerProfile() {
//        return "Welcome Customer";
//    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getMyProfile(email));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updateMyProfile(email, request));
    }

    //using this only Admin can get the details of the any customer
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //Feature: Change User Role (Admin Only)
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {

        return ResponseEntity.ok(userService.updateUserRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deactivateUser(@PathVariable Long id) {

        return ResponseEntity.ok(userService.deactivateUser(id));
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