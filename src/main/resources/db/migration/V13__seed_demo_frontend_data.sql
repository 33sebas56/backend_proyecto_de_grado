-- V13__seed_demo_frontend_data.sql
-- Datos DEMO para que el frontend tenga empresas, convenios, dashboard,
-- aprobaciones pendientes y estados variados antes del despliegue.
-- Password de todos los usuarios demo: password
-- IMPORTANTE: Esta migracion no sube archivos reales; los documentos se cargan
-- por el flujo publico de empresa o por el endpoint manual ADMIN.

DO $$
DECLARE
    v_password_hash TEXT := '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'; -- password

    v_admin UUID := '00000000-0000-0000-0000-000000000101';
    v_profesor UUID := '00000000-0000-0000-0000-000000000102';
    v_proyeccion UUID := '00000000-0000-0000-0000-000000000103';
    v_juridico UUID := '00000000-0000-0000-0000-000000000104';
    v_rectoria UUID := '00000000-0000-0000-0000-000000000105';
    v_rector_medellin UUID := '00000000-0000-0000-0000-000000000106';

    v_company_salud UUID := '10000000-0000-0000-0000-000000000201';
    v_company_tecnologia UUID := '10000000-0000-0000-0000-000000000202';
    v_company_social UUID := '10000000-0000-0000-0000-000000000203';
    v_company_bienestar UUID := '10000000-0000-0000-0000-000000000204';
    v_company_pendiente UUID := '10000000-0000-0000-0000-000000000205';
    v_company_observada UUID := '10000000-0000-0000-0000-000000000206';

    v_conv_borrador UUID := '20000000-0000-0000-0000-000000000301';
    v_conv_docs UUID := '20000000-0000-0000-0000-000000000302';
    v_conv_listo UUID := '20000000-0000-0000-0000-000000000303';
    v_conv_revision_proy UUID := '20000000-0000-0000-0000-000000000304';
    v_conv_revision_jur UUID := '20000000-0000-0000-0000-000000000305';
    v_conv_aprobado UUID := '20000000-0000-0000-0000-000000000306';
    v_conv_formalizado UUID := '20000000-0000-0000-0000-000000000307';
    v_conv_vencido UUID := '20000000-0000-0000-0000-000000000308';

    v_ver_borrador UUID := '30000000-0000-0000-0000-000000000401';
    v_ver_docs UUID := '30000000-0000-0000-0000-000000000402';
    v_ver_listo UUID := '30000000-0000-0000-0000-000000000403';
    v_ver_revision_proy UUID := '30000000-0000-0000-0000-000000000404';
    v_ver_revision_jur UUID := '30000000-0000-0000-0000-000000000405';
    v_ver_aprobado UUID := '30000000-0000-0000-0000-000000000406';
    v_ver_formalizado UUID := '30000000-0000-0000-0000-000000000407';
    v_ver_vencido UUID := '30000000-0000-0000-0000-000000000408';

    v_round_proy UUID := '40000000-0000-0000-0000-000000000501';
    v_round_jur UUID := '40000000-0000-0000-0000-000000000502';
    v_round_aprobado UUID := '40000000-0000-0000-0000-000000000503';

    v_step_proy_pending UUID := '50000000-0000-0000-0000-000000000601';
    v_step_jur_proy_ok UUID := '50000000-0000-0000-0000-000000000602';
    v_step_jur_pending UUID := '50000000-0000-0000-0000-000000000603';
    v_step_apr_proy UUID := '50000000-0000-0000-0000-000000000604';
    v_step_apr_jur UUID := '50000000-0000-0000-0000-000000000605';
    v_step_apr_rector UUID := '50000000-0000-0000-0000-000000000606';
BEGIN
    -- Usuarios institucionales demo.
    INSERT INTO users (id, full_name, email, password_hash, email_verified, active, auth_provider, created_at, updated_at)
    VALUES
        (v_admin, 'Admin Demo UCC', 'admin.demo@campusucc.edu.co', v_password_hash, TRUE, TRUE, 'LOCAL', now(), now()),
        (v_profesor, 'Profesor Demo UCC', 'profesor.demo@campusucc.edu.co', v_password_hash, TRUE, TRUE, 'LOCAL', now(), now()),
        (v_proyeccion, 'Gestor Proyeccion Demo', 'proyeccion.demo@campusucc.edu.co', v_password_hash, TRUE, TRUE, 'LOCAL', now(), now()),
        (v_juridico, 'Revisor Juridico Demo', 'juridico.demo@campusucc.edu.co', v_password_hash, TRUE, TRUE, 'LOCAL', now(), now()),
        (v_rectoria, 'Rectoria Demo', 'rectoria.demo@campusucc.edu.co', v_password_hash, TRUE, TRUE, 'LOCAL', now(), now()),
        (v_rector_medellin, 'Rector Medellin Demo', 'rector.medellin.demo@campusucc.edu.co', v_password_hash, TRUE, TRUE, 'LOCAL', now(), now())
    ON CONFLICT (email) DO NOTHING;

    -- Roles demo.
    INSERT INTO user_roles (user_id, role_id)
    SELECT v_admin, id FROM roles WHERE name = 'ADMIN'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT v_profesor, id FROM roles WHERE name = 'PROFESOR'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT v_proyeccion, id FROM roles WHERE name = 'GESTOR_PROYECCION'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT v_juridico, id FROM roles WHERE name = 'REVISOR_JURIDICO'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT v_rectoria, id FROM roles WHERE name = 'RECTORIA'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO user_roles (user_id, role_id)
    SELECT v_rector_medellin, id FROM roles WHERE name = 'RECTOR_MEDELLIN'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    -- Perfiles revisores para bandejas de trabajo y asignaciones.
    INSERT INTO reviewer_profiles (user_id, role_id, available, max_active_cases, current_active_cases, notes, seal_name, created_at, updated_at)
    SELECT v_proyeccion, id, TRUE, 8, 1, 'Perfil demo para Proyeccion Social', 'Gestor de Proyeccion Social UCC', now(), now()
    FROM roles WHERE name = 'GESTOR_PROYECCION'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO reviewer_profiles (user_id, role_id, available, max_active_cases, current_active_cases, notes, seal_name, created_at, updated_at)
    SELECT v_juridico, id, TRUE, 8, 1, 'Perfil demo para revision juridica', 'Revisor Juridico UCC', now(), now()
    FROM roles WHERE name = 'REVISOR_JURIDICO'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO reviewer_profiles (user_id, role_id, available, max_active_cases, current_active_cases, notes, seal_name, created_at, updated_at)
    SELECT v_rectoria, id, TRUE, 5, 0, 'Perfil demo para Rectoria', 'Rectoria UCC', now(), now()
    FROM roles WHERE name = 'RECTORIA'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    INSERT INTO reviewer_profiles (user_id, role_id, available, max_active_cases, current_active_cases, notes, seal_name, created_at, updated_at)
    SELECT v_rector_medellin, id, TRUE, 5, 0, 'Perfil demo para Rector Medellin', 'Rector Sede Medellin', now(), now()
    FROM roles WHERE name = 'RECTOR_MEDELLIN'
    ON CONFLICT (user_id, role_id) DO NOTHING;

    -- Empresas demo para listas, filtros y validacion juridica.
    INSERT INTO companies (id, nit, business_name, trade_name, identification_type, legal_representative_name, contact_email, contact_phone, address, status, created_by, validated_by, validated_at, created_at, updated_at)
    VALUES
        (v_company_salud, '900111222-1', 'Clinica Salud Integral S.A.S.', 'Salud Integral', 'NIT', 'Laura Mejia Torres', 'contacto.salud.demo@example.com', '3001112233', 'Cra 45 # 10-25, Medellin', 'VALIDADA', v_profesor, v_juridico, now() - interval '30 days', now() - interval '45 days', now() - interval '30 days'),
        (v_company_tecnologia, '901222333-2', 'TechNova Soluciones S.A.S.', 'TechNova', 'NIT', 'Andres Rios Gomez', 'legal.technova.demo@example.com', '3002223344', 'Calle 80 # 45-12, Medellin', 'VALIDADA', v_profesor, v_juridico, now() - interval '25 days', now() - interval '35 days', now() - interval '25 days'),
        (v_company_social, '902333444-3', 'Fundacion Comunidad Viva', 'Comunidad Viva', 'NIT', 'Diana Marcela Pena', 'alianzas.comunidad.demo@example.com', '3003334455', 'Calle 33 # 78-19, Medellin', 'VALIDADA', v_proyeccion, v_juridico, now() - interval '20 days', now() - interval '32 days', now() - interval '20 days'),
        (v_company_bienestar, '903444555-4', 'Bienestar Empresarial Colombia S.A.S.', 'Beco', 'NIT', 'Felipe Andrade Ruiz', 'convenios.bienestar.demo@example.com', '3004445566', 'Transversal 51 # 67-08, Medellin', 'VALIDADA', v_proyeccion, v_juridico, now() - interval '15 days', now() - interval '28 days', now() - interval '15 days'),
        (v_company_pendiente, '904555666-5', 'Laboratorio Futuro S.A.S.', 'Lab Futuro', 'NIT', 'Camila Rojas Velez', 'contacto.labfuturo.demo@example.com', '3005556677', 'Calle 10 # 42-70, Medellin', 'PENDIENTE_VALIDACION', v_profesor, NULL, NULL, now() - interval '6 days', now() - interval '6 days'),
        (v_company_observada, '905666777-6', 'Centro Cultural Horizonte', 'Horizonte', 'NIT', 'Mario Esteban Lopez', 'admin.horizonte.demo@example.com', '3006667788', 'Cra 70 # 49-31, Medellin', 'OBSERVADA', v_profesor, v_juridico, now() - interval '3 days', now() - interval '10 days', now() - interval '3 days')
    ON CONFLICT (nit) DO NOTHING;

    -- Convenios en diferentes estados para tarjetas, filtros y tablas del frontend.
    INSERT INTO convenios (id, code, company_id, created_by, current_status, current_stage, start_date, end_date, revision_issue_count, convenio_type, created_at, updated_at)
    VALUES
        (v_conv_borrador, 'DEMO-2026-0001', v_company_salud, v_profesor, 'BORRADOR', NULL, NULL, NULL, 0, 'PRACTICA', now() - interval '20 days', now() - interval '20 days'),
        (v_conv_docs, 'DEMO-2026-0002', v_company_tecnologia, v_profesor, 'PENDIENTE_DOCUMENTOS_EMPRESA', NULL, NULL, NULL, 0, 'PRACTICA', now() - interval '18 days', now() - interval '16 days'),
        (v_conv_listo, 'DEMO-2026-0003', v_company_social, v_profesor, 'LISTO_PARA_RADICAR', NULL, NULL, NULL, 0, 'PRACTICA', now() - interval '15 days', now() - interval '10 days'),
        (v_conv_revision_proy, 'DEMO-2026-0004', v_company_bienestar, v_profesor, 'EN_REVISION', 'PROYECCION', NULL, NULL, 0, 'MARCO', now() - interval '12 days', now() - interval '2 days'),
        (v_conv_revision_jur, 'DEMO-2026-0005', v_company_salud, v_profesor, 'EN_REVISION', 'JURIDICA', NULL, NULL, 0, 'PRACTICA', now() - interval '11 days', now() - interval '1 day'),
        (v_conv_aprobado, 'DEMO-2026-0006', v_company_tecnologia, v_profesor, 'APROBADO_PARA_FIRMA', NULL, NULL, NULL, 0, 'MARCO', now() - interval '10 days', now() - interval '12 hours'),
        (v_conv_formalizado, 'DEMO-2026-0007', v_company_social, v_proyeccion, 'FORMALIZADO', NULL, current_date - 20, current_date + 345, 0, 'BIENESTAR', now() - interval '40 days', now() - interval '20 days'),
        (v_conv_vencido, 'DEMO-2026-0008', v_company_bienestar, v_proyeccion, 'VENCIDO', NULL, current_date - 400, current_date - 35, 0, 'DESCUENTO', now() - interval '450 days', now() - interval '35 days')
    ON CONFLICT (code) DO NOTHING;

    -- Versiones actuales de los convenios.
    INSERT INTO convenio_versions (id, convenio_id, version_number, title, objective, description, duration_months, start_date, end_date, external_entity_obligations, university_obligations, estimated_value, status, created_by, created_at, reason)
    VALUES
        (v_ver_borrador, v_conv_borrador, 1, 'Convenio de practica Clinica Salud Integral', 'Fortalecer escenarios de practica para estudiantes de salud.', 'Convenio demo en borrador para probar formularios.', 12, NULL, NULL, 'Disponer escenarios de practica y tutor institucional.', 'Acompanar seguimiento academico y evaluacion.', 0, 'BORRADOR', v_profesor, now() - interval '20 days', 'CREACION_INICIAL'),
        (v_ver_docs, v_conv_docs, 1, 'Convenio de practica TechNova', 'Habilitar espacios de practica en areas de tecnologia.', 'Convenio pendiente de documentos de empresa.', 6, NULL, NULL, 'Asignar mentor tecnico y espacios de aprendizaje.', 'Realizar seguimiento academico.', 0, 'BORRADOR', v_profesor, now() - interval '18 days', 'CREACION_INICIAL'),
        (v_ver_listo, v_conv_listo, 1, 'Convenio de practica Fundacion Comunidad Viva', 'Articular practica social con proyectos comunitarios.', 'Convenio con documentos aprobados y listo para radicar.', 12, NULL, NULL, 'Facilitar proyectos comunitarios y supervision en campo.', 'Acompanar el proceso formativo.', 0, 'BORRADOR', v_profesor, now() - interval '15 days', 'CREACION_INICIAL'),
        (v_ver_revision_proy, v_conv_revision_proy, 1, 'Convenio marco Bienestar Empresarial Colombia', 'Establecer relacion marco para actividades de cooperacion.', 'Convenio marco en revision por Proyeccion Social.', 24, NULL, NULL, 'Participar en actividades de cooperacion.', 'Gestionar articulacion institucional.', 0, 'VIGENTE', v_profesor, now() - interval '12 days', 'CREACION_INICIAL'),
        (v_ver_revision_jur, v_conv_revision_jur, 1, 'Convenio de practica Clinica Salud Integral 2026', 'Actualizar condiciones de practica para nuevo periodo academico.', 'Convenio en etapa juridica.', 12, NULL, NULL, 'Garantizar condiciones del escenario de practica.', 'Verificar cumplimiento academico.', 0, 'VIGENTE', v_profesor, now() - interval '11 days', 'CREACION_INICIAL'),
        (v_ver_aprobado, v_conv_aprobado, 1, 'Convenio marco TechNova 2026', 'Definir marco de cooperacion tecnologica.', 'Convenio aprobado para firma y pendiente de formalizar.', 24, NULL, NULL, 'Apoyar proyectos tecnologicos conjuntos.', 'Coordinar equipos academicos y administrativos.', 0, 'FINAL', v_profesor, now() - interval '10 days', 'VERSION_FINAL_APROBADA'),
        (v_ver_formalizado, v_conv_formalizado, 1, 'Convenio de bienestar Comunidad Viva', 'Desarrollar actividades de bienestar y extension.', 'Convenio formalizado y vigente.', 12, current_date - 20, current_date + 345, 'Ejecutar actividades acordadas.', 'Gestionar acompanamiento y seguimiento.', 0, 'FINAL', v_proyeccion, now() - interval '40 days', 'VERSION_FINAL_APROBADA'),
        (v_ver_vencido, v_conv_vencido, 1, 'Convenio de descuento Bienestar Empresarial', 'Ofrecer beneficios a comunidad universitaria.', 'Convenio demo vencido.', 12, current_date - 400, current_date - 35, 'Mantener beneficios durante la vigencia.', 'Divulgar condiciones a la comunidad.', 0, 'FINAL', v_proyeccion, now() - interval '450 days', 'VERSION_FINAL_APROBADA')
    ON CONFLICT (convenio_id, version_number) DO NOTHING;

    -- Enlazar version actual.
    UPDATE convenios SET current_version_id = v_ver_borrador WHERE id = v_conv_borrador;
    UPDATE convenios SET current_version_id = v_ver_docs WHERE id = v_conv_docs;
    UPDATE convenios SET current_version_id = v_ver_listo WHERE id = v_conv_listo;
    UPDATE convenios SET current_version_id = v_ver_revision_proy WHERE id = v_conv_revision_proy;
    UPDATE convenios SET current_version_id = v_ver_revision_jur WHERE id = v_conv_revision_jur;
    UPDATE convenios SET current_version_id = v_ver_aprobado WHERE id = v_conv_aprobado;
    UPDATE convenios SET current_version_id = v_ver_formalizado WHERE id = v_conv_formalizado;
    UPDATE convenios SET current_version_id = v_ver_vencido WHERE id = v_conv_vencido;

    -- Historial para actividad reciente.
    INSERT INTO convenio_status_history (convenio_id, previous_status, new_status, previous_stage, new_stage, comment, performed_by, performed_at)
    VALUES
        (v_conv_borrador, NULL, 'BORRADOR', NULL, NULL, 'Convenio demo creado en borrador.', v_profesor, now() - interval '20 days'),
        (v_conv_docs, 'BORRADOR', 'PENDIENTE_DOCUMENTOS_EMPRESA', NULL, NULL, 'Solicitud documental enviada a empresa demo.', v_profesor, now() - interval '16 days'),
        (v_conv_listo, 'DOCUMENTOS_APROBADOS', 'LISTO_PARA_RADICAR', NULL, NULL, 'Documentos de empresa aprobados.', v_profesor, now() - interval '10 days'),
        (v_conv_revision_proy, 'RADICADO', 'EN_REVISION', NULL, 'PROYECCION', 'Convenio radicado y asignado a Proyeccion Social.', v_profesor, now() - interval '2 days'),
        (v_conv_revision_jur, 'EN_REVISION', 'EN_REVISION', 'PROYECCION', 'JURIDICA', 'Proyeccion aprobada; pasa a revision juridica.', v_proyeccion, now() - interval '1 day'),
        (v_conv_aprobado, 'EN_REVISION', 'APROBADO_PARA_FIRMA', 'RECTORIA', NULL, 'Rectoria aprobo el convenio; queda pendiente de formalizacion.', v_rector_medellin, now() - interval '12 hours'),
        (v_conv_formalizado, 'APROBADO_PARA_FIRMA', 'FORMALIZADO', NULL, NULL, 'Convenio formalizado por Proyeccion Social.', v_proyeccion, now() - interval '20 days'),
        (v_conv_vencido, 'FORMALIZADO', 'VENCIDO', NULL, NULL, 'Convenio marcado como vencido por fin de vigencia.', v_admin, now() - interval '35 days')
    ON CONFLICT DO NOTHING;

    -- Rondas y pasos para que el frontend vea pendientes reales por rol.
    INSERT INTO approval_rounds (id, convenio_id, convenio_version_id, round_number, status, started_at, finished_at)
    VALUES
        (v_round_proy, v_conv_revision_proy, v_ver_revision_proy, 1, 'EN_PROCESO', now() - interval '2 days', NULL),
        (v_round_jur, v_conv_revision_jur, v_ver_revision_jur, 1, 'EN_PROCESO', now() - interval '3 days', NULL),
        (v_round_aprobado, v_conv_aprobado, v_ver_aprobado, 1, 'APROBADA', now() - interval '9 days', now() - interval '12 hours')
    ON CONFLICT (convenio_id, round_number) DO NOTHING;

    INSERT INTO approval_steps (id, approval_round_id, stage, stage_order, assigned_user_id, status, decision_comment, assigned_at, responded_at, approval_code, seal_text, due_at)
    VALUES
        (v_step_proy_pending, v_round_proy, 'PROYECCION', 1, v_proyeccion, 'PENDIENTE', NULL, now() - interval '2 days', NULL, NULL, NULL, now() + interval '5 days'),
        (v_step_jur_proy_ok, v_round_jur, 'PROYECCION', 1, v_proyeccion, 'APROBADO', 'Viable desde Proyeccion Social.', now() - interval '3 days', now() - interval '1 day', 'DEMO-PROY-001', 'Aprobado por Proyeccion Social Demo', now() + interval '3 days'),
        (v_step_jur_pending, v_round_jur, 'JURIDICA', 2, v_juridico, 'PENDIENTE', NULL, now() - interval '1 day', NULL, NULL, NULL, now() + interval '6 days'),
        (v_step_apr_proy, v_round_aprobado, 'PROYECCION', 1, v_proyeccion, 'APROBADO', 'Aprobado por Proyeccion Social.', now() - interval '9 days', now() - interval '8 days', 'DEMO-PROY-002', 'Aprobado por Proyeccion Social Demo', now() - interval '2 days'),
        (v_step_apr_jur, v_round_aprobado, 'JURIDICA', 2, v_juridico, 'APROBADO', 'Aprobado juridicamente.', now() - interval '8 days', now() - interval '5 days', 'DEMO-JUR-001', 'Aprobado por Juridica Demo', now() - interval '1 day'),
        (v_step_apr_rector, v_round_aprobado, 'RECTORIA', 3, v_rector_medellin, 'APROBADO', 'Aprobado por Rector Medellin.', now() - interval '5 days', now() - interval '12 hours', 'DEMO-RECTOR-001', 'Aprobado por Rector Medellin Demo', now() + interval '1 day')
    ON CONFLICT (approval_round_id, stage) DO NOTHING;

    -- Alertas personales para campanita y my-work.
    INSERT INTO review_alerts (approval_step_id, convenio_id, recipient_user_id, alert_type, audience, title, message, created_at, read_at)
    VALUES
        (v_step_proy_pending, v_conv_revision_proy, v_proyeccion, 'PRIMER_RECORDATORIO', 'REVISOR', 'Revision pendiente en Proyeccion', 'Tienes un convenio marco demo pendiente por revisar.', now() - interval '1 day', NULL),
        (v_step_jur_pending, v_conv_revision_jur, v_juridico, 'PRIMER_RECORDATORIO', 'REVISOR', 'Revision juridica pendiente', 'Tienes un convenio de practica demo pendiente por revisar.', now() - interval '10 hours', NULL),
        (NULL, v_conv_aprobado, v_proyeccion, 'INCIDENCIA_REGISTRADA', 'PROYECCION_SOCIAL', 'Convenio pendiente de formalizacion', 'El convenio DEMO-2026-0006 fue aprobado para firma y esta pendiente de formalizacion.', now() - interval '8 hours', NULL)
    ON CONFLICT DO NOTHING;
END $$;
