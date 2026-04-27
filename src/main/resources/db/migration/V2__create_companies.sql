CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    nit VARCHAR(30) NOT NULL UNIQUE,

    business_name VARCHAR(200) NOT NULL,

    trade_name VARCHAR(200),

    identification_type VARCHAR(50) NOT NULL DEFAULT 'NIT',

    legal_representative_name VARCHAR(150),

    contact_email VARCHAR(180),

    contact_phone VARCHAR(50),

    address VARCHAR(255),

    status VARCHAR(40) NOT NULL DEFAULT 'BORRADOR',

    created_by UUID NOT NULL,

    validated_by UUID,

    validated_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_companies_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT fk_companies_validated_by
        FOREIGN KEY (validated_by)
        REFERENCES users(id),

    CONSTRAINT chk_companies_status
        CHECK (status IN (
            'BORRADOR',
            'PENDIENTE_VALIDACION',
            'VALIDADA',
            'OBSERVADA',
            'RECHAZADA'
        ))
);

CREATE TABLE company_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    company_id UUID NOT NULL,

    document_type VARCHAR(60) NOT NULL,

    current_version_id UUID,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_company_documents_company
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_company_documents_type
        CHECK (document_type IN (
            'RUT',
            'CAMARA_COMERCIO',
            'CEDULA_REPRESENTANTE',
            'CERTIFICADO_EXISTENCIA',
            'OTRO'
        ))
);

CREATE TABLE company_document_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    company_document_id UUID NOT NULL,

    version_number INTEGER NOT NULL,

    file_url TEXT NOT NULL,

    storage_path TEXT NOT NULL,

    original_filename VARCHAR(255) NOT NULL,

    mime_type VARCHAR(120),

    file_size BIGINT,

    uploaded_by UUID NOT NULL,

    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_company_document_versions_document
        FOREIGN KEY (company_document_id)
        REFERENCES company_documents(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_company_document_versions_uploaded_by
        FOREIGN KEY (uploaded_by)
        REFERENCES users(id),

    CONSTRAINT uq_company_document_version
        UNIQUE (company_document_id, version_number),

    CONSTRAINT chk_company_document_versions_version
        CHECK (version_number > 0),

    CONSTRAINT chk_company_document_versions_file_size
        CHECK (file_size IS NULL OR file_size >= 0)
);

ALTER TABLE company_documents
ADD CONSTRAINT fk_company_documents_current_version
FOREIGN KEY (current_version_id)
REFERENCES company_document_versions(id);

CREATE TABLE company_validation_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    company_id UUID NOT NULL,

    previous_status VARCHAR(40),

    new_status VARCHAR(40) NOT NULL,

    comment TEXT,

    performed_by UUID NOT NULL,

    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_company_validation_history_company
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_company_validation_history_performed_by
        FOREIGN KEY (performed_by)
        REFERENCES users(id),

    CONSTRAINT chk_company_validation_history_previous_status
        CHECK (
            previous_status IS NULL OR previous_status IN (
                'BORRADOR',
                'PENDIENTE_VALIDACION',
                'VALIDADA',
                'OBSERVADA',
                'RECHAZADA'
            )
        ),

    CONSTRAINT chk_company_validation_history_new_status
        CHECK (new_status IN (
            'BORRADOR',
            'PENDIENTE_VALIDACION',
            'VALIDADA',
            'OBSERVADA',
            'RECHAZADA'
        ))
);