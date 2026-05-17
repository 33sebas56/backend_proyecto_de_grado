INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'sebastian.ramostoro@campusucc.edu.co'
ON CONFLICT (user_id, role_id) DO NOTHING;