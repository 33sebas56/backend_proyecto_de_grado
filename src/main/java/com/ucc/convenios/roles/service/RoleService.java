package com.ucc.convenios.roles.service;

import com.ucc.convenios.roles.dto.RoleResponse;
import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.RoleRepository;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.ucc.convenios.users.entity.ReviewerProfile;
import com.ucc.convenios.users.repository.ReviewerProfileRepository;

import java.util.Set;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ReviewerProfileRepository reviewerProfileRepository;

    public RoleService(
            RoleRepository roleRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            ReviewerProfileRepository reviewerProfileRepository
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.reviewerProfileRepository = reviewerProfileRepository;
    }

    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(RoleResponse::fromEntity)
                .toList();
    }

    public void assignRoleToUser(String email, String roleName) {
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedRoleName = roleName.trim().toUpperCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Role role = roleRepository.findByName(normalizedRoleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            throw new BadRequestException("El usuario ya tiene asignado este rol");
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepository.save(userRole);

        createReviewerProfileIfNeeded(user, role);
    }
    private void createReviewerProfileIfNeeded(User user, Role role) {
        if (!isReviewerRole(role.getName())) {
            return;
        }

        if (reviewerProfileRepository.existsByUserAndRole(user, role)) {
            return;
        }

        ReviewerProfile reviewerProfile = new ReviewerProfile();
        reviewerProfile.setUser(user);
        reviewerProfile.setRole(role);
        reviewerProfile.setAvailable(true);
        reviewerProfile.setMaxActiveCases(5);
        reviewerProfile.setCurrentActiveCases(0);

        reviewerProfileRepository.save(reviewerProfile);
    }

    private boolean isReviewerRole(String roleName) {
        Set<String> reviewerRoles = Set.of(
                "GESTOR_PROYECCION",
                "REVISOR_JURIDICO",
                "REVISOR_FINANCIERO",
                "RECTORIA"
        );

        return reviewerRoles.contains(roleName);
    }
}