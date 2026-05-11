UPDATE companies
SET contact_email = 'pendiente-correo-' || replace(id::text, '-', '') || '@example.invalid'
WHERE contact_email IS NULL OR trim(contact_email) = '';

ALTER TABLE companies
ALTER COLUMN contact_email SET NOT NULL;

CREATE TABLE company_document_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    convenio_id UUID NOT NULL,

    company_id UUID NOT NULL,

    round_number INTEGER NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE_EMPRESA',

    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    submitted_at TIMESTAMP,

    reviewed_at TIMESTAMP,

    reviewed_by UUID,

    review_comment TEXT,

    CONSTRAINT fk_company_document_requests_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_company_document_requests_company
        FOREIGN KEY (company_id)
        REFERENCES companies(id),

    CONSTRAINT fk_company_document_requests_reviewed_by
        FOREIGN KEY (reviewed_by)
        REFERENCES users(id),

    CONSTRAINT uq_company_document_requests_round
        UNIQUE (convenio_id, round_number),

    CONSTRAINT chk_company_document_requests_round
        CHECK (round_number BETWEEN 1 AND 6),

    CONSTRAINT chk_company_document_requests_status
        CHECK (status IN (
            'PENDIENTE_EMPRESA',
            'DOCUMENTOS_RECIBIDOS',
            'OBSERVADA',
            'APROBADA',
            'CANCELADA',
            'VENCIDA'
        ))
);

CREATE TABLE company_upload_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    request_id UUID NOT NULL,

    convenio_id UUID NOT NULL,

    company_id UUID NOT NULL,

    token_hash VARCHAR(128) NOT NULL UNIQUE,

    recipient_email VARCHAR(180) NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    revoked_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by UUID NOT NULL,

    CONSTRAINT fk_company_upload_tokens_request
        FOREIGN KEY (request_id)
        REFERENCES company_document_requests(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_company_upload_tokens_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_company_upload_tokens_company
        FOREIGN KEY (company_id)
        REFERENCES companies(id),

    CONSTRAINT fk_company_upload_tokens_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
);

CREATE INDEX idx_company_upload_tokens_token_hash
ON company_upload_tokens(token_hash);

CREATE INDEX idx_company_upload_tokens_expires_at
ON company_upload_tokens(expires_at);

CREATE TABLE company_submitted_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    request_id UUID NOT NULL,

    convenio_id UUID NOT NULL,

    document_type VARCHAR(80) NOT NULL,

    display_name VARCHAR(150) NOT NULL,

    original_filename VARCHAR(255) NOT NULL,

    mime_type VARCHAR(120),

    file_size BIGINT,

    storage_path TEXT NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'SUBIDO',

    review_comment TEXT,

    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    approved_at TIMESTAMP,

    deleted_from_storage_at TIMESTAMP,

    CONSTRAINT fk_company_submitted_documents_request
        FOREIGN KEY (request_id)
        REFERENCES company_document_requests(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_company_submitted_documents_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_company_submitted_documents_type
        CHECK (document_type IN (
            'CEDULA_REPRESENTANTE',
            'RUT_O_RUNT',
            'DOCUMENTO_ADICIONAL_1',
            'DOCUMENTO_ADICIONAL_2',
            'DOCUMENTO_ADICIONAL_3'
        )),

    CONSTRAINT chk_company_submitted_documents_status
        CHECK (status IN (
            'SUBIDO',
            'APROBADO',
            'OBSERVADO',
            'REEMPLAZADO',
            'ELIMINADO'
        )),

    CONSTRAINT chk_company_submitted_documents_file_size
        CHECK (file_size IS NULL OR file_size >= 0)
);

CREATE INDEX idx_company_submitted_documents_convenio
ON company_submitted_documents(convenio_id);

CREATE INDEX idx_company_submitted_documents_request
ON company_submitted_documents(request_id);

ALTER TABLE review_alerts
DROP CONSTRAINT IF EXISTS chk_review_alerts_type;

ALTER TABLE review_alerts
ADD CONSTRAINT chk_review_alerts_type
CHECK (alert_type IN (
    'PRIMER_RECORDATORIO',
    'SEGUNDO_RECORDATORIO',
    'ULTIMO_RECORDATORIO',
    'REVISION_VENCIDA',
    'INCIDENCIA_REGISTRADA',
    'LIMITE_INCIDENCIAS_ALCANZADO',
    'DOCUMENTOS_EMPRESA_RECIBIDOS',
    'LIMITE_CORRECCIONES_TEMPRANAS'
));