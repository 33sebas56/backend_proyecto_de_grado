package com.ucc.convenios.roles.service;

import com.ucc.convenios.notifications.service.AppLinkService;
import com.ucc.convenios.notifications.service.EmailTemplateService;
import com.ucc.convenios.notifications.service.MailService;
import com.ucc.convenios.roles.dto.RoleResponse;
import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.RoleRepository;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.ReviewerProfile;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.ReviewerProfileRepository;
import com.ucc.convenios.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_PROFESOR = "PROFESOR";
    private static final String ROLE_GESTOR_PROYECCION = "GESTOR_PROYECCION";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ReviewerProfileRepository reviewerProfileRepository;
    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;
    private final AppLinkService appLinkService;

    public RoleService(
            RoleRepository roleRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            ReviewerProfileRepository reviewerProfileRepository,
            MailService mailService,
            EmailTemplateService emailTemplateService,
            AppLinkService appLinkService
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.reviewerProfileRepository = reviewerProfileRepository;
        this.mailService = mailService;
        this.emailTemplateService = emailTemplateService;
        this.appLinkService = appLinkService;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(RoleResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void assignRoleToUser(String email, String roleName) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedRoleName = normalizeRoleName(roleName);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Role role = roleRepository.findByName(normalizedRoleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            throw new BadRequestException("El usuario ya tiene asignado este rol");
        }

        validateRoleCompatibility(user, normalizedRoleName);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepository.save(userRole);

        createReviewerProfileIfNeeded(user, role);

        sendRoleAssignedEmail(user, role);
    }

    @Transactional
    public void removeRoleFromUser(String email, String roleName) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedRoleName = normalizeRoleName(roleName);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Role role = roleRepository.findByName(normalizedRoleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        validateRoleCanBeRemoved(role);

        UserRole userRole = userRoleRepository.findByUserAndRole(user, role)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no tiene asignado este rol"));

        removeReviewerProfileIfNeeded(user, role);

        userRoleRepository.delete(userRole);
    }

    private void validateRoleCompatibility(User user, String roleNameToAssign) {
        boolean userHasProfesor = userHasRole(user, ROLE_PROFESOR);
        boolean userHasGestorProyeccion = userHasRole(user, ROLE_GESTOR_PROYECCION);

        if (ROLE_PROFESOR.equals(roleNameToAssign) && userHasGestorProyeccion) {
            throw new BadRequestException("No se puede asignar PROFESOR porque el usuario ya tiene GESTOR_PROYECCION");
        }

        if (ROLE_GESTOR_PROYECCION.equals(roleNameToAssign) && userHasProfesor) {
            throw new BadRequestException("No se puede asignar GESTOR_PROYECCION porque el usuario ya tiene PROFESOR");
        }
    }

    private void validateRoleCanBeRemoved(Role role) {
        if (ROLE_ADMIN.equals(role.getName())) {
            throw new BadRequestException("No se puede quitar el rol ADMIN desde este endpoint");
        }
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

    private void removeReviewerProfileIfNeeded(User user, Role role) {
        if (!isReviewerRole(role.getName())) {
            return;
        }

        reviewerProfileRepository.findByUserAndRole(user, role)
                .ifPresent(profile -> {
                    Integer activeCases = profile.getCurrentActiveCases();

                    if (activeCases != null && activeCases > 0) {
                        throw new BadRequestException("No se puede quitar el rol porque el revisor tiene casos activos");
                    }

                    reviewerProfileRepository.delete(profile);
                });
    }

    private void sendRoleAssignedEmail(User user, Role role) {
        try {
            String subject = emailTemplateService.buildRoleAssignedSubject(role.getName());
            String htmlBody = emailTemplateService.buildRoleAssignedHtml(
                    user.getFullName(),
                    role.getName(),
                    appLinkService.buildSystemUrl()
            );

            mailService.sendHtmlEmail(user.getEmail(), subject, htmlBody);
        } catch (Exception exception) {
            LOGGER.warn(
                    "No se pudo enviar el correo de asignación de rol a {}. El rol quedó asignado correctamente.",
                    user.getEmail(),
                    exception
            );
        }
    }

    private boolean userHasRole(User user, String roleName) {
        return userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .anyMatch(roleName::equals);
    }

    private boolean isReviewerRole(String roleName) {
        Set<String> reviewerRoles = Set.of(
                "GESTOR_PROYECCION",
                "REVISOR_JURIDICO",
                "RECTORIA",
                "RECTOR_MEDELLIN"
        );

        return reviewerRoles.contains(roleName);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El correo del usuario es obligatorio");
        }

        return email.trim().toLowerCase();
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new BadRequestException("El rol es obligatorio");
        }

        return roleName.trim().toUpperCase();
    }
}