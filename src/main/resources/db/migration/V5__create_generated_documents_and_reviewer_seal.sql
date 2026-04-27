ALTER TABLE reviewer_profiles
ADD COLUMN seal_name VARCHAR(120);

CREATE TABLE convenio_generated_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    convenio_id UUID NOT NULL,

    convenio_version_id UUID NOT NULL,

    approval_step_id UUID,

    document_type VARCHAR(60) NOT NULL,

    stage VARCHAR(40),

    file_name VARCHAR(255) NOT NULL,

    storage_path TEXT NOT NULL,

    url TEXT NOT NULL,

    generated_by UUID NOT NULL,

    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    notes TEXT,

    CONSTRAINT fk_convenio_generated_documents_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_convenio_generated_documents_version
        FOREIGN KEY (convenio_version_id)
        REFERENCES convenio_versions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_convenio_generated_documents_step
        FOREIGN KEY (approval_step_id)
        REFERENCES approval_steps(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_convenio_generated_documents_user
        FOREIGN KEY (generated_by)
        REFERENCES users(id),

    CONSTRAINT chk_convenio_generated_documents_type
        CHECK (document_type IN (
            'RADICADO',
            'FINAL_APROBADO',
            'CORRECCION_SOLICITADA',
            'RECHAZADO'
        )),

    CONSTRAINT chk_convenio_generated_documents_stage
        CHECK (
            stage IS NULL OR stage IN (
                'PROYECCION',
                'JURIDICA',
                'FINANCIERA',
                'RECTORIA'
            )
        )
);