CREATE TABLE email_verification_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    email VARCHAR(180) NOT NULL,

    code_hash VARCHAR(255) NOT NULL,

    purpose VARCHAR(40) NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    used_at TIMESTAMP,

    attempts INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_email_verification_codes_purpose
        CHECK (purpose IN (
            'REGISTER',
            'LOGIN',
            'PASSWORD_RESET'
        )),

    CONSTRAINT chk_email_verification_codes_attempts
        CHECK (attempts >= 0)
);

CREATE INDEX idx_email_verification_codes_email_purpose
ON email_verification_codes(email, purpose);

CREATE INDEX idx_email_verification_codes_expires_at
ON email_verification_codes(expires_at);