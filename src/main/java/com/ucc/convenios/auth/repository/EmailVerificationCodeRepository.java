package com.ucc.convenios.auth.repository;

import com.ucc.convenios.auth.entity.EmailVerificationCode;
import com.ucc.convenios.shared.enums.EmailCodePurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, UUID> {

    Optional<EmailVerificationCode> findFirstByEmailAndPurposeAndUsedAtIsNullOrderByCreatedAtDesc(
            String email,
            EmailCodePurpose purpose
    );
}