package com.ucc.convenios.users.controller;

import com.ucc.convenios.users.dto.UserResponse;
import com.ucc.convenios.users.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findAllUsers() {
        return userService.findAllUsers();
    }
}