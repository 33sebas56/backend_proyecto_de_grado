ALTER TABLE convenios
ADD COLUMN revision_issue_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE convenios
ADD CONSTRAINT chk_convenios_revision_issue_count
CHECK (revision_issue_count >= 0);

ALTER TABLE approval_steps
ADD COLUMN due_at TIMESTAMP;

ALTER TABLE approval_steps
ADD COLUMN first_reminder_sent_at TIMESTAMP;

ALTER TABLE approval_steps
ADD COLUMN second_reminder_sent_at TIMESTAMP;

ALTER TABLE approval_steps
ADD COLUMN final_reminder_sent_at TIMESTAMP;

ALTER TABLE approval_steps
ADD COLUMN expired_at TIMESTAMP;

ALTER TABLE approval_steps
DROP CONSTRAINT IF EXISTS chk_approval_steps_status;

ALTER TABLE approval_steps
ADD CONSTRAINT chk_approval_steps_status
CHECK (status IN (
    'PENDIENTE',
    'APROBADO',
    'CORRECCION_SOLICITADA',
    'RECHAZADO',
    'CANCELADO',
    'VENCIDO'
));

ALTER TABLE convenio_generated_documents
DROP CONSTRAINT IF EXISTS chk_convenio_generated_documents_type;

ALTER TABLE convenio_generated_documents
ADD CONSTRAINT chk_convenio_generated_documents_type
CHECK (document_type IN (
    'RADICADO',
    'FINAL_APROBADO',
    'CORRECCION_SOLICITADA',
    'RECHAZADO',
    'REVISION_VENCIDA'
));

CREATE TABLE review_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    approval_step_id UUID,

    convenio_id UUID NOT NULL,

    recipient_user_id UUID,

    alert_type VARCHAR(60) NOT NULL,

    audience VARCHAR(60) NOT NULL,

    title VARCHAR(180) NOT NULL,

    message TEXT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    read_at TIMESTAMP,

    CONSTRAINT fk_review_alerts_step
        FOREIGN KEY (approval_step_id)
        REFERENCES approval_steps(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_review_alerts_convenio
        FOREIGN KEY (convenio_id)
        REFERENCES convenios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_alerts_recipient_user
        FOREIGN KEY (recipient_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_review_alerts_type
        CHECK (alert_type IN (
            'PRIMER_RECORDATORIO',
            'SEGUNDO_RECORDATORIO',
            'ULTIMO_RECORDATORIO',
            'REVISION_VENCIDA',
            'INCIDENCIA_REGISTRADA',
            'LIMITE_INCIDENCIAS_ALCANZADO'
        )),

    CONSTRAINT chk_review_alerts_audience
        CHECK (audience IN (
            'REVISOR',
            'SOLICITANTE',
            'ADMIN',
            'PROYECCION_SOCIAL'
        ))
);