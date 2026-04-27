package com.ucc.convenios.auth.controller;

import com.ucc.convenios.auth.dto.AuthResponse;
import com.ucc.convenios.auth.dto.LoginRequest;
import com.ucc.convenios.auth.dto.RegisterWithCodeRequest;
import com.ucc.convenios.auth.dto.RequestRegisterCodeRequest;
import com.ucc.convenios.auth.service.AuthService;
import com.ucc.convenios.auth.service.EmailVerificationCodeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationCodeService emailVerificationCodeService;

    public AuthController(
            AuthService authService,
            EmailVerificationCodeService emailVerificationCodeService
    ) {
        this.authService = authService;
        this.emailVerificationCodeService = emailVerificationCodeService;
    }

    @PostMapping("/register-code/request")
    public String requestRegisterCode(@Valid @RequestBody RequestRegisterCodeRequest request) {
        emailVerificationCodeService.requestRegisterCode(request);
        return "Código de verificación enviado al correo";
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterWithCodeRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}