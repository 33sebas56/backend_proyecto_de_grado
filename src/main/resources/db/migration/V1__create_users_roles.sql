CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    full_name VARCHAR(150) NOT NULL,

    email VARCHAR(180) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    auth_provider VARCHAR(30) NOT NULL DEFAULT 'LOCAL',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_users_email_campusucc
        CHECK (LOWER(email) LIKE '%@campusucc.edu.co'),

    CONSTRAINT chk_users_auth_provider
        CHECK (auth_provider IN ('LOCAL', 'MICROSOFT'))
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(60) NOT NULL UNIQUE,

    description VARCHAR(255),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    role_id UUID NOT NULL,

    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_roles_user_role
        UNIQUE (user_id, role_id)
);

CREATE TABLE reviewer_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    role_id UUID NOT NULL,

    available BOOLEAN NOT NULL DEFAULT TRUE,

    max_active_cases INTEGER NOT NULL DEFAULT 5,

    current_active_cases INTEGER NOT NULL DEFAULT 0,

    notes VARCHAR(255),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reviewer_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reviewer_profiles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_reviewer_profiles_user_role
        UNIQUE (user_id, role_id),

    CONSTRAINT chk_reviewer_profiles_max_cases
        CHECK (max_active_cases >= 0),

    CONSTRAINT chk_reviewer_profiles_current_cases
        CHECK (current_active_cases >= 0)
);

INSERT INTO roles (name, description) VALUES
('SOLICITANTE', 'Usuario institucional que puede registrar empresas y convenios'),
('ADMIN', 'Administrador del sistema'),
('GESTOR_PROYECCION', 'Responsable de la revisión inicial de convenios'),
('REVISOR_JURIDICO', 'Responsable de la validación jurídica de empresas y convenios'),
('REVISOR_FINANCIERO', 'Responsable de la revisión financiera de convenios'),
('RECTORIA', 'Responsable de la revisión o cierre final del convenio');