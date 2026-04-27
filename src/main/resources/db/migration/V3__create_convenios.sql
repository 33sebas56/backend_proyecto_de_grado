CREATE TABLE convenios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code VARCHAR(40) NOT NULL UNIQUE,

    company_id UUID NOT NULL,

    created_by UUID NOT NULL,

    current_status VARCHAR(40) NOT NULL DEFAULT 'BORRADOR',

    current_stage VARCHAR(40),

    current_version_id UUID,

    start_date DATE,

    end_date DATE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_convenios_company
        FOREIGN KEY (company_id)
        REFERENCES companies(id),

    CONSTRAINT fk_convenios_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT chk_convenios_status
        CHECK (current_status IN (
            'BORRADOR',
            'EMPRESA_PENDIENTE',
            'RADICADO',
            'EN_REVISION',
            'EN_CORRECCION',
            'APROBADO_PARA_FIRMA',
            'FORMALIZADO',
            'RECHAZADO',
            'DESISTIDO',
            'VENCIDO',
            'CERRADO'
        )),

    CONSTRAINT chk_convenios_stage
        CHECK (
            current_stage IS NULL OR current_stage IN (
                'PROYECCION',
                'JURIDICA',
                'FINANCIERA',
                'RECTORIA'
            )
        )
);

CREATE TABLE convenio_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    convenio_id UUID NOT NULL,

    version_number INTEGER NOT NULL,

    title VARCHAR(200) NOT NULL,

    objective TEXT NOT NULL,

    description TEXT,

    duration_months INTEGER,

    start_date DATE,

    end_date DATE,

    external_entity_obligations TEXT,

    university_obligations TEXT,

    estimated_value NUMERIC(15, 2),

    generated_pdf_url TEXT,

    generated_pdf_storage_path TEXT,

    status VARCHAR(40) NOT NULL DEFAULT 'BORRADOR',

    created_by UUID NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    reason VARCHAR(80) NOT NULL DEFAULT 'CREACION_INICIAL',

    CONSTRAINT fk_convenio_versions_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_convenio_versions_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT uq_convenio_version
        UNIQUE (convenio_id, version_number),

    CONSTRAINT chk_convenio_versions_version_number
        CHECK (version_number > 0),

    CONSTRAINT chk_convenio_versions_duration
        CHECK (duration_months IS NULL OR duration_months > 0),

    CONSTRAINT chk_convenio_versions_value
        CHECK (estimated_value IS NULL OR estimated_value >= 0),

    CONSTRAINT chk_convenio_versions_status
        CHECK (status IN (
            'BORRADOR',
            'VIGENTE',
            'HISTORICA',
            'FINAL'
        )),

    CONSTRAINT chk_convenio_versions_reason
        CHECK (reason IN (
            'CREACION_INICIAL',
            'CORRECCION_SOLICITADA',
            'VERSION_FINAL_APROBADA'
        ))
);

ALTER TABLE convenios
ADD CONSTRAINT fk_convenios_current_version
FOREIGN KEY (current_version_id)
REFERENCES convenio_versions(id);

CREATE TABLE convenio_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    convenio_id UUID NOT NULL,

    previous_status VARCHAR(40),

    new_status VARCHAR(40) NOT NULL,

    previous_stage VARCHAR(40),

    new_stage VARCHAR(40),

    comment TEXT,

    performed_by UUID NOT NULL,

    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_convenio_status_history_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_convenio_status_history_performed_by
        FOREIGN KEY (performed_by)
        REFERENCES users(id),

    CONSTRAINT chk_convenio_status_history_previous_status
        CHECK (
            previous_status IS NULL OR previous_status IN (
                'BORRADOR',
                'EMPRESA_PENDIENTE',
                'RADICADO',
                'EN_REVISION',
                'EN_CORRECCION',
                'APROBADO_PARA_FIRMA',
                'FORMALIZADO',
                'RECHAZADO',
                'DESISTIDO',
                'VENCIDO',
                'CERRADO'
            )
        ),

    CONSTRAINT chk_convenio_status_history_new_status
        CHECK (new_status IN (
            'BORRADOR',
            'EMPRESA_PENDIENTE',
            'RADICADO',
            'EN_REVISION',
            'EN_CORRECCION',
            'APROBADO_PARA_FIRMA',
            'FORMALIZADO',
            'RECHAZADO',
            'DESISTIDO',
            'VENCIDO',
            'CERRADO'
        )),

    CONSTRAINT chk_convenio_status_history_previous_stage
        CHECK (
            previous_stage IS NULL OR previous_stage IN (
                'PROYECCION',
                'JURIDICA',
                'FINANCIERA',
                'RECTORIA'
            )
        ),

    CONSTRAINT chk_convenio_status_history_new_stage
        CHECK (
            new_stage IS NULL OR new_stage IN (
                'PROYECCION',
                'JURIDICA',
                'FINANCIERA',
                'RECTORIA'
            )
        )
);