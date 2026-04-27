package com.ucc.convenios.auth.service;

import com.ucc.convenios.auth.dto.AuthResponse;
import com.ucc.convenios.auth.dto.LoginRequest;
import com.ucc.convenios.auth.dto.RegisterWithCodeRequest;
import com.ucc.convenios.auth.security.CustomUserDetailsService;
import com.ucc.convenios.auth.security.JwtService;
import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.RoleRepository;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.enums.AuthProvider;
import com.ucc.convenios.shared.enums.RoleName;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.dto.UserResponse;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private static final String INSTITUTIONAL_DOMAIN = "@campusucc.edu.co";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmailVerificationCodeService emailVerificationCodeService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService,
            EmailVerificationCodeService emailVerificationCodeService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.emailVerificationCodeService = emailVerificationCodeService;
    }

    public AuthResponse register(RegisterWithCodeRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        validateInstitutionalEmail(normalizedEmail);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Ya existe un usuario registrado con este correo");
        }

        emailVerificationCodeService.verifyRegisterCode(normalizedEmail, request.getCode());

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(true);
        user.setActive(true);
        user.setAuthProvider(AuthProvider.LOCAL);

        User savedUser = userRepository.save(user);

        Role defaultRole = roleRepository.findByName(RoleName.SOLICITANTE.name())
                .orElseThrow(() -> new ResourceNotFoundException("Rol SOLICITANTE no encontrado"));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(defaultRole);
        userRoleRepository.save(userRole);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtService.generateToken(userDetails);

        List<String> roles = List.of(defaultRole.getName());
        UserResponse userResponse = UserResponse.fromEntity(savedUser, roles);

        return new AuthResponse(token, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        if (!user.getActive()) {
            throw new BadRequestException("El usuario se encuentra inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        List<String> roles = userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .toList();

        UserResponse userResponse = UserResponse.fromEntity(user, roles);

        return new AuthResponse(token, userResponse);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void validateInstitutionalEmail(String email) {
        if (!email.endsWith(INSTITUTIONAL_DOMAIN)) {
            throw new BadRequestException("Solo se permiten correos institucionales terminados en " + INSTITUTIONAL_DOMAIN);
        }
    }
}