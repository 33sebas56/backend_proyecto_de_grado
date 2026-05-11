ALTER TABLE convenios
ADD COLUMN IF NOT EXISTS convenio_type VARCHAR(40);

UPDATE convenios
SET convenio_type = 'MARCO'
WHERE convenio_type IS NULL OR trim(convenio_type) = '';

ALTER TABLE convenios
ALTER COLUMN convenio_type SET NOT NULL;

ALTER TABLE convenios
DROP CONSTRAINT IF EXISTS chk_convenios_type;

ALTER TABLE convenios
ADD CONSTRAINT chk_convenios_type
CHECK (
    convenio_type IN (
        'MARCO',
        'PRACTICA',
        'BIENESTAR',
        'DESCUENTO'
    )
);

CREATE INDEX IF NOT EXISTS idx_convenios_type
ON convenios(convenio_type);