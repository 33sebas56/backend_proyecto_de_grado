CREATE TABLE approval_rounds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    convenio_id UUID NOT NULL,

    convenio_version_id UUID NOT NULL,

    round_number INTEGER NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'EN_PROCESO',

    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    finished_at TIMESTAMP,

    CONSTRAINT fk_approval_rounds_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_approval_rounds_convenio_version
        FOREIGN KEY (convenio_version_id)
        REFERENCES convenio_versions(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_approval_round_convenio_round
        UNIQUE (convenio_id, round_number),

    CONSTRAINT chk_approval_rounds_round_number
        CHECK (round_number > 0),

    CONSTRAINT chk_approval_rounds_status
        CHECK (status IN (
            'EN_PROCESO',
            'APROBADA',
            'RECHAZADA',
            'CANCELADA_POR_NUEVA_VERSION'
        ))
);

CREATE TABLE approval_steps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    approval_round_id UUID NOT NULL,

    stage VARCHAR(40) NOT NULL,

    stage_order INTEGER NOT NULL,

    assigned_user_id UUID NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',

    decision_comment TEXT,

    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    responded_at TIMESTAMP,

    approval_code VARCHAR(80),

    seal_text TEXT,

    CONSTRAINT fk_approval_steps_round
        FOREIGN KEY (approval_round_id)
        REFERENCES approval_rounds(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_approval_steps_assigned_user
        FOREIGN KEY (assigned_user_id)
        REFERENCES users(id),

    CONSTRAINT uq_approval_steps_round_stage
        UNIQUE (approval_round_id, stage),

    CONSTRAINT chk_approval_steps_stage
        CHECK (stage IN (
            'PROYECCION',
            'JURIDICA',
            'FINANCIERA',
            'RECTORIA'
        )),

    CONSTRAINT chk_approval_steps_order
        CHECK (stage_order > 0),

    CONSTRAINT chk_approval_steps_status
        CHECK (status IN (
            'PENDIENTE',
            'APROBADO',
            'CORRECCION_SOLICITADA',
            'RECHAZADO',
            'CANCELADO'
        ))
);