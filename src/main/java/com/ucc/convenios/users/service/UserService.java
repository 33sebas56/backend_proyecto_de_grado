package com.ucc.convenios.users.service;

import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.dto.UserResponse;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public UserResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<String> roles = userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(role -> role.getName())
                .toList();

        return UserResponse.fromEntity(user, roles);
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    List<String> roles = userRoleRepository.findByUser(user)
                            .stream()
                            .map(UserRole::getRole)
                            .map(role -> role.getName())
                            .toList();

                    return UserResponse.fromEntity(user, roles);
                })
                .toList();
    }
}