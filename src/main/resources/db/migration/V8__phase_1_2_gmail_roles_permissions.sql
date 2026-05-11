-- Fase 1 y 2: Gmail SMTP, nuevo rol PROFESOR y salida de FINANCIERA del flujo activo.
-- No se reescriben migraciones antiguas. Se agregan cambios incrementales.

INSERT INTO roles (name, description)
VALUES ('PROFESOR', 'Profesor proponente que puede crear convenios y revisar documentos externos de sus propios convenios')
ON CONFLICT (name) DO NOTHING;

UPDATE roles
SET description = 'Usuario institucional base sin permiso activo para crear convenios'
WHERE name = 'SOLICITANTE';

UPDATE roles
SET description = 'Rol financiero legado; no participa en el flujo activo de aprobaciones'
WHERE name = 'REVISOR_FINANCIERO';

ALTER TABLE convenios
DROP CONSTRAINT IF EXISTS chk_convenios_stage;

ALTER TABLE convenios
ADD CONSTRAINT chk_convenios_stage
CHECK (
    current_stage IS NULL OR current_stage IN (
        'PROYECCION',
        'JURIDICA',
        'RECTORIA'
    )
);

ALTER TABLE convenio_status_history
DROP CONSTRAINT IF EXISTS chk_convenio_status_history_previous_stage;

ALTER TABLE convenio_status_history
ADD CONSTRAINT chk_convenio_status_history_previous_stage
CHECK (
    previous_stage IS NULL OR previous_stage IN (
        'PROYECCION',
        'JURIDICA',
        'RECTORIA'
    )
);

ALTER TABLE convenio_status_history
DROP CONSTRAINT IF EXISTS chk_convenio_status_history_new_stage;

ALTER TABLE convenio_status_history
ADD CONSTRAINT chk_convenio_status_history_new_stage
CHECK (
    new_stage IS NULL OR new_stage IN (
        'PROYECCION',
        'JURIDICA',
        'RECTORIA'
    )
);

ALTER TABLE approval_steps
DROP CONSTRAINT IF EXISTS chk_approval_steps_stage;

ALTER TABLE approval_steps
ADD CONSTRAINT chk_approval_steps_stage
CHECK (
    stage IN (
        'PROYECCION',
        'JURIDICA',
        'RECTORIA'
    )
);

ALTER TABLE convenio_generated_documents
DROP CONSTRAINT IF EXISTS chk_convenio_generated_documents_stage;

ALTER TABLE convenio_generated_documents
ADD CONSTRAINT chk_convenio_generated_documents_stage
CHECK (
    stage IS NULL OR stage IN (
        'PROYECCION',
        'JURIDICA',
        'RECTORIA'
    )
);

ALTER TABLE convenios
DROP CONSTRAINT IF EXISTS chk_convenios_status;

ALTER TABLE convenios
ADD CONSTRAINT chk_convenios_status
CHECK (
    current_status IN (
        'BORRADOR',
        'EMPRESA_PENDIENTE',
        'PENDIENTE_DOCUMENTOS_EMPRESA',
        'DOCUMENTOS_EMPRESA_RECIBIDOS',
        'DOCUMENTOS_OBSERVADOS_EMPRESA',
        'DOCUMENTOS_APROBADOS',
        'LISTO_PARA_RADICAR',
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
);

ALTER TABLE convenio_status_history
DROP CONSTRAINT IF EXISTS chk_convenio_status_history_previous_status;

ALTER TABLE convenio_status_history
ADD CONSTRAINT chk_convenio_status_history_previous_status
CHECK (
    previous_status IS NULL OR previous_status IN (
        'BORRADOR',
        'EMPRESA_PENDIENTE',
        'PENDIENTE_DOCUMENTOS_EMPRESA',
        'DOCUMENTOS_EMPRESA_RECIBIDOS',
        'DOCUMENTOS_OBSERVADOS_EMPRESA',
        'DOCUMENTOS_APROBADOS',
        'LISTO_PARA_RADICAR',
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
);

ALTER TABLE convenio_status_history
DROP CONSTRAINT IF EXISTS chk_convenio_status_history_new_status;

ALTER TABLE convenio_status_history
ADD CONSTRAINT chk_convenio_status_history_new_status
CHECK (
    new_status IN (
        'BORRADOR',
        'EMPRESA_PENDIENTE',
        'PENDIENTE_DOCUMENTOS_EMPRESA',
        'DOCUMENTOS_EMPRESA_RECIBIDOS',
        'DOCUMENTOS_OBSERVADOS_EMPRESA',
        'DOCUMENTOS_APROBADOS',
        'LISTO_PARA_RADICAR',
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
);