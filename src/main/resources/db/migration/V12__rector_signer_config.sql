
INSERT INTO roles (name, description)
VALUES (
    'RECTOR_MEDELLIN',
    'Rector sede Medellín responsable de la aprobación final de convenios marco, bienestar y descuento'
)
ON CONFLICT (name) DO NOTHING;