## 2. Roles principales

```text
SOLICITANTE       Rol base inicial. No opera el flujo.
ADMIN             Gestiona usuarios, roles, dashboard general y carga manual administrativa.
PROFESOR          Crea empresa, crea convenio, solicita documentos, revisa documentos y radica.
GESTOR_PROYECCION Revisa etapa Proyección y formaliza convenios aprobados para firma.
REVISOR_JURIDICO  Valida empresas y revisa etapa Jurídica.
RECTORIA          Aprueba etapa final de convenios PRACTICA.
RECTOR_MEDELLIN   Aprueba etapa final de MARCO, BIENESTAR y DESCUENTO.
```

---

## 3. Flujo general esperado

```text
1. Usuario institucional solicita código de registro.
2. Usuario completa registro con código.
3. ADMIN asigna rol.
4. PROFESOR o GESTOR_PROYECCION crea empresa.
5. Empresa se envía a validación.
6. REVISOR_JURIDICO o ADMIN valida empresa.
7. PROFESOR o GESTOR_PROYECCION crea convenio.
8. Se solicita documentación a la empresa.
9. Empresa externa recibe correo y carga documentos por token público.
10. Interno revisa y aprueba documentos.
11. Convenio queda LISTO_PARA_RADICAR.
12. Se radica convenio.
13. Se aprueba Proyección, Jurídica y Rectoría/Rector Medellín.
14. Convenio queda APROBADO_PARA_FIRMA.
15. Proyección Social formaliza.
16. Convenio queda FORMALIZADO con startDate y endDate calculadas.
```

---

# 4. Autenticación

## 4.1 Solicitar código de registro

```http
POST {{baseUrl}}/api/auth/register-code/request
```

### Body

```json
{
  "email": "sebastian.ramostoro@campusucc.edu.co"
}
```

### Response

```text
Código de verificación enviado al correo
```

---

## 4.2 Registrar usuario con código

```http
POST {{baseUrl}}/api/auth/register
```

### Body

```json
{
  "fullName": "Sebastian Ramos Toro",
  "email": "sebastian.ramostoro@campusucc.edu.co",
  "password": "Password123*",
  "code": "123456"
}
```

### Response

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "user": {
    "id": "uuid",
    "fullName": "Sebastian Ramos Toro",
    "email": "sebastian.ramostoro@campusucc.edu.co",
    "emailVerified": true,
    "active": true,
    "authProvider": "LOCAL",
    "roles": ["SOLICITANTE"],
    "createdAt": "2026-05-15T10:00:00"
  }
}
```

---

## 4.3 Login

```http
POST {{baseUrl}}/api/auth/login
```

### Body

```json
{
  "email": "sebastian.ramostoro@campusucc.edu.co",
  "password": "Password123*"
}
```

### Response

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "user": {
    "id": "uuid",
    "fullName": "Sebastian Ramos Toro",
    "email": "sebastian.ramostoro@campusucc.edu.co",
    "emailVerified": true,
    "active": true,
    "authProvider": "LOCAL",
    "roles": ["ADMIN", "PROFESOR"],
    "createdAt": "2026-05-15T10:00:00"
  }
}
```

Guardar el token en la variable correspondiente de Postman.

---

# 5. Usuarios

## 5.1 Usuario autenticado

```http
GET {{baseUrl}}/api/users/me
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
{
  "id": "uuid",
  "fullName": "Sebastian Ramos Toro",
  "email": "sebastian.ramostoro@campusucc.edu.co",
  "emailVerified": true,
  "active": true,
  "authProvider": "LOCAL",
  "roles": ["ADMIN", "PROFESOR"],
  "createdAt": "2026-05-15T10:00:00"
}
```

---

## 5.2 Listar usuarios

```http
GET {{baseUrl}}/api/users
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "fullName": "Sebastian Ramos Toro",
    "email": "sebastian.ramostoro@campusucc.edu.co",
    "emailVerified": true,
    "active": true,
    "authProvider": "LOCAL",
    "roles": ["ADMIN", "PROFESOR"],
    "createdAt": "2026-05-15T10:00:00"
  }
]
```

---

# 6. Roles

## 6.1 Listar roles

```http
GET {{baseUrl}}/api/roles
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "name": "ADMIN",
    "description": "Administrador del sistema"
  },
  {
    "id": "uuid",
    "name": "PROFESOR",
    "description": "Profesor solicitante"
  },
  {
    "id": "uuid",
    "name": "GESTOR_PROYECCION",
    "description": "Gestor de Proyección Social"
  }
]
```

---

## 6.2 Asignar rol

```http
POST {{baseUrl}}/api/roles/assign
Authorization: Bearer {{tokenAdmin}}
```

### Body

```json
{
  "email": "cristhian.orbes@campusucc.edu.co",
  "roleName": "GESTOR_PROYECCION"
}
```

### Response

```text
204 No Content
```

---

## 6.3 Quitar rol

```http
POST {{baseUrl}}/api/roles/remove
Authorization: Bearer {{tokenAdmin}}
```

### Body

```json
{
  "email": "cristhian.orbes@campusucc.edu.co",
  "roleName": "GESTOR_PROYECCION"
}
```

### Response

```text
204 No Content
```

---

# 7. Empresas

## 7.1 Crear empresa

```http
POST {{baseUrl}}/api/companies
Authorization: Bearer {{tokenProfesor}}
```

### Body

```json
{
  "nit": "900123456-7",
  "businessName": "Empresa Prueba Convenios SAS",
  "tradeName": "Empresa Prueba",
  "identificationType": "NIT",
  "legalRepresentativeName": "Sergio Ceballos",
  "contactEmail": "sebas-ramos2018@outlook.com",
  "contactPhone": "3001234567",
  "address": "Cartagena, Colombia"
}
```

### Response

```json
{
  "id": "uuid",
  "nit": "900123456-7",
  "businessName": "Empresa Prueba Convenios SAS",
  "tradeName": "Empresa Prueba",
  "identificationType": "NIT",
  "legalRepresentativeName": "Sergio Ceballos",
  "contactEmail": "sebas-ramos2018@outlook.com",
  "contactPhone": "3001234567",
  "address": "Cartagena, Colombia",
  "status": "BORRADOR",
  "createdById": "uuid",
  "validatedById": null,
  "validatedAt": null,
  "createdAt": "2026-05-15T10:00:00",
  "updatedAt": "2026-05-15T10:00:00"
}
```

---

## 7.2 Listar empresas

```http
GET {{baseUrl}}/api/companies
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "nit": "900123456-7",
    "businessName": "Empresa Prueba Convenios SAS",
    "tradeName": "Empresa Prueba",
    "identificationType": "NIT",
    "legalRepresentativeName": "Sergio Ceballos",
    "contactEmail": "sebas-ramos2018@outlook.com",
    "contactPhone": "3001234567",
    "address": "Cartagena, Colombia",
    "status": "VALIDADA",
    "createdById": "uuid",
    "validatedById": "uuid",
    "validatedAt": "2026-05-15T11:00:00",
    "createdAt": "2026-05-15T10:00:00",
    "updatedAt": "2026-05-15T11:00:00"
  }
]
```

---

## 7.3 Consultar empresa por ID

```http
GET {{baseUrl}}/api/companies/{{companyId}}
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

Devuelve un objeto `CompanyResponse`.

```json
{
  "id": "uuid",
  "nit": "900123456-7",
  "businessName": "Empresa Prueba Convenios SAS",
  "tradeName": "Empresa Prueba",
  "identificationType": "NIT",
  "legalRepresentativeName": "Sergio Ceballos",
  "contactEmail": "sebas-ramos2018@outlook.com",
  "contactPhone": "3001234567",
  "address": "Cartagena, Colombia",
  "status": "VALIDADA",
  "createdById": "uuid",
  "validatedById": "uuid",
  "validatedAt": "2026-05-15T11:00:00",
  "createdAt": "2026-05-15T10:00:00",
  "updatedAt": "2026-05-15T11:00:00"
}
```

---

## 7.4 Buscar empresa por NIT

```http
GET {{baseUrl}}/api/companies/by-nit/900123456-7
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

Devuelve un objeto `CompanyResponse`.

---

## 7.5 Empresas pendientes de validación

```http
GET {{baseUrl}}/api/companies/pending-validation
Authorization: Bearer {{tokenJuridico}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "nit": "900123456-7",
    "businessName": "Empresa Prueba Convenios SAS",
    "status": "PENDIENTE_VALIDACION",
    "createdById": "uuid",
    "validatedById": null,
    "validatedAt": null,
    "createdAt": "2026-05-15T10:00:00",
    "updatedAt": "2026-05-15T10:30:00"
  }
]
```

---

## 7.6 Enviar empresa a validación

```http
POST {{baseUrl}}/api/companies/{{companyId}}/submit-validation
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

Devuelve `CompanyResponse` con estado `PENDIENTE_VALIDACION`.

---

## 7.7 Validar empresa

```http
POST {{baseUrl}}/api/companies/{{companyId}}/validate
Authorization: Bearer {{tokenJuridico}}
```

### Body

```json
{
  "comment": "Empresa validada correctamente."
}
```

### Response

Devuelve `CompanyResponse` con estado `VALIDADA`.

---

## 7.8 Observar empresa

```http
POST {{baseUrl}}/api/companies/{{companyId}}/observe
Authorization: Bearer {{tokenJuridico}}
```

### Body

```json
{
  "comment": "Falta actualizar datos de contacto."
}
```

### Response

Devuelve `CompanyResponse` con estado `OBSERVADA`.

---

## 7.9 Rechazar empresa

```http
POST {{baseUrl}}/api/companies/{{companyId}}/reject
Authorization: Bearer {{tokenJuridico}}
```

### Body

```json
{
  "comment": "La empresa no cumple los requisitos."
}
```

### Response

Devuelve `CompanyResponse` con estado `RECHAZADA`.

---

## 7.10 Historial de empresa

```http
GET {{baseUrl}}/api/companies/{{companyId}}/history
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "companyId": "uuid",
    "previousStatus": "PENDIENTE_VALIDACION",
    "newStatus": "VALIDADA",
    "comment": "Empresa validada correctamente.",
    "performedById": "uuid",
    "performedAt": "2026-05-15T11:00:00"
  }
]
```

---

# 8. Convenios

## 8.1 Crear convenio

```http
POST {{baseUrl}}/api/convenios
Authorization: Bearer {{tokenProfesor}}
Content-Type: application/json
```

Tambien puede crear convenios un usuario con rol `GESTOR_PROYECCION`, segun permisos del backend.

### Body actualizado

Al crear convenio se envia la duracion en meses. No se envian `startDate` ni `endDate`, porque esas fechas se calculan despues, cuando Proyeccion Social formaliza el convenio.

```json
{
  "companyId": "{{companyId}}",
  "convenioType": "PRACTICA",
  "title": "Convenio de practica empresarial",
  "objective": "Establecer condiciones para el desarrollo de practicas profesionales.",
  "description": "Convenio academico para practicas estudiantiles.",
  "durationMonths": 12,
  "externalEntityObligations": "Asignar tutor, permitir actividades y entregar documentacion requerida.",
  "universityObligations": "Acompanar proceso academico y hacer seguimiento.",
  "estimatedValue": 0
}
```

Valores permitidos para `convenioType`:

```text
MARCO
PRACTICA
BIENESTAR
DESCUENTO
```

### Response

```json
{
  "id": "uuid",
  "code": "CONV-2026-0001",
  "companyId": "uuid",
  "companyNit": "900123456-7",
  "companyBusinessName": "Empresa Prueba Convenios SAS",
  "createdById": "uuid",
  "currentStatus": "BORRADOR",
  "currentStage": null,
  "convenioType": "PRACTICA",
  "convenioTypeLabel": "Convenio de practicas",
  "rectorSignerLabel": "Rector de la institucion",
  "currentVersionId": "uuid",
  "currentVersionNumber": 1,
  "title": "Convenio de practica empresarial",
  "objective": "Establecer condiciones para el desarrollo de practicas profesionales.",
  "description": "Convenio academico para practicas estudiantiles.",
  "durationMonths": 12,
  "externalEntityObligations": "Asignar tutor, permitir actividades y entregar documentacion requerida.",
  "universityObligations": "Acompanar proceso academico y hacer seguimiento.",
  "estimatedValue": 0,
  "canEditBeforeReview": true,
  "startDate": null,
  "endDate": null,
  "createdAt": "2026-05-15T12:00:00",
  "updatedAt": "2026-05-15T12:00:00",
  "revisionIssueCount": 0
}
```

---

## 8.2 Listar convenios

```http
GET {{baseUrl}}/api/convenios
Authorization: Bearer {{tokenAdmin}}
```

Tambien puede ser consumido por usuarios internos autenticados segun permisos del backend.

### Body

No lleva body.

### Response

Devuelve una lista de convenios. Cada item incluye datos generales del convenio y datos principales de la version actual, como `title`, `objective`, `description`, `durationMonths`, obligaciones y valor estimado.

```json
[
  {
    "id": "uuid",
    "code": "CONV-2026-0001",
    "companyId": "uuid",
    "companyNit": "900123456-7",
    "companyBusinessName": "Empresa Prueba Convenios SAS",
    "createdById": "uuid",
    "currentStatus": "PENDIENTE_DOCUMENTOS_EMPRESA",
    "currentStage": null,
    "convenioType": "PRACTICA",
    "convenioTypeLabel": "Convenio de practicas",
    "rectorSignerLabel": "Rector de la institucion",
    "currentVersionId": "uuid",
    "currentVersionNumber": 1,
    "title": "Convenio de practica empresarial",
    "objective": "Establecer condiciones para el desarrollo de practicas profesionales.",
    "description": "Convenio academico para practicas estudiantiles.",
    "durationMonths": 12,
    "externalEntityObligations": "Asignar tutor, permitir actividades y entregar documentacion requerida.",
    "universityObligations": "Acompanar proceso academico y hacer seguimiento.",
    "estimatedValue": 0,
    "canEditBeforeReview": true,
    "startDate": null,
    "endDate": null,
    "createdAt": "2026-05-15T12:00:00",
    "updatedAt": "2026-05-15T12:30:00",
    "revisionIssueCount": 0
  }
]
```

### Notas para frontend

- `title` es el titulo que debe mostrarse en tablas, cards y buscadores de convenios.
- `currentStatus` sirve para pintar el estado general del convenio.
- `currentStage` sirve para pintar la etapa de revision formal cuando el convenio ya esta en flujo de aprobacion.
- `canEditBeforeReview` sirve para decidir si se muestra o no el boton de editar.
- `startDate` y `endDate` pueden venir `null` hasta que Proyeccion Social formalice el convenio.

---

## 8.3 Consultar convenio por ID

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

Devuelve un objeto `ConvenioResponse` con la misma estructura del listado, pero correspondiente a un solo convenio.

```json
{
  "id": "uuid",
  "code": "CONV-2026-0001",
  "companyId": "uuid",
  "companyNit": "900123456-7",
  "companyBusinessName": "Empresa Prueba Convenios SAS",
  "createdById": "uuid",
  "currentStatus": "LISTO_PARA_RADICAR",
  "currentStage": null,
  "convenioType": "PRACTICA",
  "convenioTypeLabel": "Convenio de practicas",
  "rectorSignerLabel": "Rector de la institucion",
  "currentVersionId": "uuid",
  "currentVersionNumber": 1,
  "title": "Convenio de practica empresarial",
  "objective": "Establecer condiciones para el desarrollo de practicas profesionales.",
  "description": "Convenio academico para practicas estudiantiles.",
  "durationMonths": 12,
  "externalEntityObligations": "Asignar tutor, permitir actividades y entregar documentacion requerida.",
  "universityObligations": "Acompanar proceso academico y hacer seguimiento.",
  "estimatedValue": 0,
  "canEditBeforeReview": true,
  "startDate": null,
  "endDate": null,
  "createdAt": "2026-05-15T12:00:00",
  "updatedAt": "2026-05-15T12:30:00",
  "revisionIssueCount": 0
}
```

---

## 8.4 Actualizar convenio antes de revision formal

```http
PUT {{baseUrl}}/api/convenios/{{convenioId}}
Authorization: Bearer {{tokenProfesor}}
Content-Type: application/json
```

### Para que sirve

Permite corregir datos del convenio antes de enviarlo a revision formal. Es decir, antes de radicarlo y antes de que entre a Proyeccion/Juridica/Rectoria.

### Quien puede editar

Puede editar el convenio:

- El usuario que creo el convenio, por ejemplo el profesor creador.
- Usuarios con rol `GESTOR_PROYECCION`.
- Usuarios con rol `ADMIN`.

Si el profesor creo el convenio, si puede editarlo antes de radicarlo. Si el profesor no fue quien lo creo, no deberia poder editarlo, salvo que tenga tambien un rol permitido como `ADMIN` o `GESTOR_PROYECCION`.

### En que estados se puede editar

El convenio se puede editar solo cuando esta antes de revision formal:

```text
BORRADOR
EMPRESA_PENDIENTE
PENDIENTE_DOCUMENTOS_EMPRESA
DOCUMENTOS_EMPRESA_RECIBIDOS
DOCUMENTOS_OBSERVADOS_EMPRESA
DOCUMENTOS_APROBADOS
LISTO_PARA_RADICAR
```

### En que estados ya NO se puede editar

No se puede editar cuando ya fue radicado o cuando ya entro al flujo formal de revision/aprobacion:

```text
RADICADO
EN_REVISION
EN_CORRECCION
APROBADO_PARA_FIRMA
FORMALIZADO
RECHAZADO
DESISTIDO
VENCIDO
CERRADO
```

### Que se puede editar

Todos los campos son opcionales. El frontend puede enviar solo los campos que quiera cambiar.

```json
{
  "companyId": "uuid",
  "convenioType": "PRACTICA",
  "title": "Convenio de practica empresarial actualizado",
  "objective": "Nuevo objeto del convenio.",
  "description": "Nueva descripcion del convenio.",
  "durationMonths": 12,
  "externalEntityObligations": "Nuevas obligaciones de la empresa.",
  "universityObligations": "Nuevas obligaciones de la universidad.",
  "estimatedValue": 0
}
```

### Reglas importantes

- `companyId` solo se puede cambiar cuando el convenio esta en `BORRADOR`.
- `companyId` debe corresponder a una empresa validada.
- `durationMonths` debe ser mayor a 0.
- `title` no puede estar vacio si se envia.
- `objective` no puede estar vacio si se envia.
- Este endpoint no cambia `currentStatus` directamente.
- Este endpoint no cambia `currentStage` directamente.
- Este endpoint no cambia `startDate` ni `endDate`.
- Este endpoint no sube ni elimina documentos.
- Para documentos se usan los endpoints de carga documental correspondientes.

### Ejemplo: actualizar solo titulo y duracion

```json
{
  "title": "Convenio de practica empresarial 2026",
  "durationMonths": 18
}
```

### Response

Devuelve el convenio actualizado.

```json
{
  "id": "uuid",
  "code": "CONV-2026-0001",
  "companyId": "uuid",
  "companyNit": "900123456-7",
  "companyBusinessName": "Empresa Prueba Convenios SAS",
  "createdById": "uuid",
  "currentStatus": "LISTO_PARA_RADICAR",
  "currentStage": null,
  "convenioType": "PRACTICA",
  "convenioTypeLabel": "Convenio de practicas",
  "rectorSignerLabel": "Rector de la institucion",
  "currentVersionId": "uuid",
  "currentVersionNumber": 1,
  "title": "Convenio de practica empresarial 2026",
  "objective": "Establecer condiciones para el desarrollo de practicas profesionales.",
  "description": "Convenio academico para practicas estudiantiles.",
  "durationMonths": 18,
  "externalEntityObligations": "Asignar tutor, permitir actividades y entregar documentacion requerida.",
  "universityObligations": "Acompanar proceso academico y hacer seguimiento.",
  "estimatedValue": 0,
  "canEditBeforeReview": true,
  "startDate": null,
  "endDate": null,
  "createdAt": "2026-05-15T12:00:00",
  "updatedAt": "2026-05-15T13:20:00",
  "revisionIssueCount": 0
}
```

### Errores comunes

```json
{
  "message": "El convenio solo se puede editar antes de enviarlo a revision formal"
}
```

```json
{
  "message": "Solo el responsable del convenio, Proyeccion Social o ADMIN pueden editar el convenio antes de enviarlo a revision"
}
```

```json
{
  "message": "La empresa solo se puede cambiar mientras el convenio este en BORRADOR"
}
```

---

## 8.5 Estados posibles del convenio

Estos son los valores posibles de `currentStatus` segun el enum `ConvenioStatus` del backend.

| Estado | Significado para frontend | Editable con PUT antes de revision |
|---|---|---|
| `BORRADOR` | Convenio creado inicialmente. Todavia no entra a flujo documental o revision formal. | Si |
| `EMPRESA_PENDIENTE` | Estado disponible para casos donde la empresa asociada aun tiene algo pendiente. | Si |
| `PENDIENTE_DOCUMENTOS_EMPRESA` | Ya se solicito documentacion a la empresa y se espera carga por token publico. | Si |
| `DOCUMENTOS_EMPRESA_RECIBIDOS` | La empresa ya cargo documentos y estan pendientes de revision interna. | Si |
| `DOCUMENTOS_OBSERVADOS_EMPRESA` | Los documentos tienen observaciones o requieren correccion. | Si |
| `DOCUMENTOS_APROBADOS` | Los documentos de la empresa ya fueron aprobados. | Si |
| `LISTO_PARA_RADICAR` | El convenio ya puede ser radicado para revision formal. | Si |
| `RADICADO` | El convenio fue radicado. Desde aqui ya no se edita por el PUT general. | No |
| `EN_REVISION` | El convenio esta en revision formal por Proyeccion, Juridica o Rectoria. | No |
| `EN_CORRECCION` | El convenio fue devuelto para correcciones desde revision formal. | No |
| `APROBADO_PARA_FIRMA` | Ya paso aprobaciones y queda pendiente de formalizacion por Proyeccion Social. | No |
| `FORMALIZADO` | Proyeccion Social formalizo el convenio. Ya tiene `startDate` y `endDate`. | No |
| `RECHAZADO` | El convenio fue rechazado. | No |
| `DESISTIDO` | El convenio fue desistido o abandonado administrativamente. | No |
| `VENCIDO` | El convenio ya supero su fecha de finalizacion. | No |
| `CERRADO` | El convenio fue cerrado administrativamente. | No |

### Etapas posibles de revision formal

El campo `currentStage` puede venir `null` o con alguno de estos valores:

```text
PROYECCION
JURIDICA
RECTORIA
```

Notas:

- `currentStage = null` significa que el convenio no esta en una etapa formal activa.
- En convenios creados por profesor, al radicar normalmente inicia en `PROYECCION`.
- En convenios creados por Proyeccion Social, al radicar puede pasar directamente a `JURIDICA`.
- La etapa final usa `RECTORIA`, pero el rol final esperado depende del tipo de convenio: `PRACTICA` usa `RECTORIA`; `MARCO`, `BIENESTAR` y `DESCUENTO` usan `RECTOR_MEDELLIN`.

---

## 8.6 Versiones del convenio

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/versions
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "convenioId": "uuid",
    "versionNumber": 1,
    "title": "Convenio de practica empresarial",
    "objective": "Establecer condiciones para el desarrollo de practicas profesionales.",
    "description": "Convenio academico para practicas estudiantiles.",
    "durationMonths": 12,
    "startDate": null,
    "endDate": null,
    "externalEntityObligations": "Asignar tutor, permitir actividades y entregar documentacion requerida.",
    "universityObligations": "Acompanar proceso academico y hacer seguimiento.",
    "estimatedValue": 0,
    "generatedPdfUrl": null,
    "generatedPdfStoragePath": null,
    "status": "VIGENTE",
    "createdById": "uuid",
    "createdAt": "2026-05-15T12:00:00",
    "reason": "CREACION_INICIAL"
  }
]
```

---

## 8.7 Historial del convenio

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/history
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "convenioId": "uuid",
    "previousStatus": "BORRADOR",
    "newStatus": "PENDIENTE_DOCUMENTOS_EMPRESA",
    "previousStage": null,
    "newStage": null,
    "comment": "Solicitud documental enviada a la empresa.",
    "performedById": "uuid",
    "performedAt": "2026-05-15T12:30:00"
  }
]
```

---

## 8.8 Radicar convenio

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/submit
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

Devuelve `ConvenioResponse`.

- Si el convenio fue creado por `PROFESOR`, al radicar debe iniciar en etapa `PROYECCION`.
- Si el convenio fue creado por `GESTOR_PROYECCION`, al radicar puede pasar directamente a `JURIDICA`, segun la logica del backend.

```json
{
  "id": "uuid",
  "code": "CONV-2026-0001",
  "currentStatus": "EN_REVISION",
  "currentStage": "PROYECCION",
  "convenioType": "PRACTICA",
  "title": "Convenio de practica empresarial",
  "durationMonths": 12,
  "startDate": null,
  "endDate": null
}
```

---

## 8.9 Formalizar convenio

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/formalize
Authorization: Bearer {{tokenGestorProyeccion}}
```

Tambien puede ejecutarlo `ADMIN`, segun permisos del backend.

### Body

No lleva body.

### Cuándo se usa

Se usa cuando el convenio esta en `APROBADO_PARA_FIRMA`. Proyeccion Social lo formaliza y el sistema calcula:

```text
startDate = fecha actual
endDate = fecha actual + durationMonths
currentStatus = FORMALIZADO
```

### Response

```json
{
  "id": "uuid",
  "code": "CONV-2026-0001",
  "companyId": "uuid",
  "companyNit": "900123456-7",
  "companyBusinessName": "Empresa Prueba Convenios SAS",
  "createdById": "uuid",
  "currentStatus": "FORMALIZADO",
  "currentStage": null,
  "convenioType": "PRACTICA",
  "convenioTypeLabel": "Convenio de practicas",
  "rectorSignerLabel": "Rector de la institucion",
  "currentVersionId": "uuid",
  "currentVersionNumber": 1,
  "title": "Convenio de practica empresarial",
  "objective": "Establecer condiciones para el desarrollo de practicas profesionales.",
  "description": "Convenio academico para practicas estudiantiles.",
  "durationMonths": 12,
  "externalEntityObligations": "Asignar tutor, permitir actividades y entregar documentacion requerida.",
  "universityObligations": "Acompanar proceso academico y hacer seguimiento.",
  "estimatedValue": 0,
  "canEditBeforeReview": false,
  "startDate": "2026-05-15",
  "endDate": "2027-05-15",
  "createdAt": "2026-05-15T12:00:00",
  "updatedAt": "2026-05-15T17:00:00",
  "revisionIssueCount": 0
}
```

---

## 8.10 Generar preview PDF

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/preview-pdf
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

```text
PDF de vista previa generado correctamente
```

---

## 8.11 Ver preview PDF

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/preview-pdf
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

```text
Archivo PDF en bytes con Content-Type: application/pdf
```

---

## 8.12 Ver PDF oficial de una versión

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/versions/{{versionId}}/pdf
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```text
Archivo PDF en bytes con Content-Type: application/pdf
```

---

## 8.13 Documentos generados del convenio

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/documents
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "convenioId": "uuid",
    "convenioVersionId": "uuid",
    "approvalStepId": "uuid",
    "documentType": "FINAL_APROBADO",
    "stage": "RECTORIA",
    "fileName": "final-aprobado-CONV-2026-0001.pdf",
    "url": "convenios/generated/.../archivo.pdf",
    "generatedById": "uuid",
    "generatedByEmail": "antonio.parra@campusucc.edu.co",
    "generatedAt": "2026-05-15T16:30:00",
    "notes": "Convenio aprobado por Rectoria"
  }
]
```

---

## 8.14 Descargar PDF generado

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/documents/{{generatedDocumentId}}/pdf
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```text
Archivo PDF en bytes con Content-Type: application/pdf
```

---

# 9. Documentos de empresa - flujo interno

## 9.1 Solicitar documentos a empresa

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/request-company-documents
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

```json
{
  "id": "uuid",
  "convenioId": "uuid",
  "companyId": "uuid",
  "companyEmail": "sebas-ramos2018@outlook.com",
  "uploadToken": "token-publico",
  "status": "PENDIENTE_EMPRESA",
  "roundNumber": 1,
  "requestedById": "uuid",
  "requestedAt": "2026-05-15T12:30:00",
  "expiresAt": "2026-05-22T12:30:00"
}
```

Guardar `uploadToken` en Postman.

---

## 9.2 Listar solicitudes documentales

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/company-document-requests
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "convenioId": "uuid",
    "companyId": "uuid",
    "companyEmail": "sebas-ramos2018@outlook.com",
    "uploadToken": "token-publico",
    "status": "PENDIENTE_EMPRESA",
    "roundNumber": 1,
    "requestedById": "uuid",
    "requestedAt": "2026-05-15T12:30:00",
    "expiresAt": "2026-05-22T12:30:00"
  }
]
```

---

## 9.3 Listar documentos cargados

```http
GET {{baseUrl}}/api/convenios/{{convenioId}}/company-documents
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "requestId": "uuid",
    "convenioId": "uuid",
    "documentType": "CEDULA_REPRESENTANTE",
    "displayName": "Cédula representante legal",
    "originalFilename": "cedula.pdf",
    "mimeType": "application/pdf",
    "fileSize": 450000,
    "status": "SUBIDO",
    "reviewComment": null,
    "uploadedAt": "2026-05-15T13:00:00",
    "approvedAt": null,
    "reviewedAt": null,
    "reviewedById": null,
    "reviewedByName": null,
    "replacedByDocumentId": null,
    "deletedFromStorageAt": null,
    "deletionReason": null
  }
]
```

---

## 9.4 Aprobar documento

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/company-documents/{{documentId}}/approve
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

Devuelve `CompanySubmittedDocumentResponse` con estado `APROBADO`.

---

## 9.5 Observar documento

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/company-documents/{{documentId}}/observe
Authorization: Bearer {{tokenProfesor}}
```

### Body

```json
{
  "comment": "El documento no es legible. Por favor cargar una versión actualizada.",
  "deletePhysicalFile": true
}
```

### Response

Devuelve `CompanySubmittedDocumentResponse` con estado `OBSERVADO` o `ELIMINADO`, según configuración del backend.

---

## 9.6 Solicitar corrección documental

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/company-documents/request-correction
Authorization: Bearer {{tokenProfesor}}
```

### Body

```json
{
  "comment": "Debe corregir los documentos observados y volver a cargarlos."
}
```

### Response

Devuelve `CompanyDocumentRequestResponse`.

---

## 9.7 Descartar proceso documental temprano

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/company-documents/discard
Authorization: Bearer {{tokenProfesor}}
```

### Body

```json
{
  "comment": "La empresa desistió del proceso."
}
```

### Response

Devuelve `CompanyDocumentRequestResponse`.

---

## 9.8 Marcar documentos aprobados

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/mark-documents-approved
Authorization: Bearer {{tokenProfesor}}
```

### Body

No lleva body.

### Response

Devuelve `CompanyDocumentRequestResponse`. El convenio debe quedar listo para radicar.

```json
{
  "id": "uuid",
  "convenioId": "uuid",
  "status": "APROBADA",
  "roundNumber": 1,
  "requestedAt": "2026-05-15T12:30:00",
  "expiresAt": "2026-05-22T12:30:00"
}
```

---

## 9.9 Carga manual de documento solo ADMIN

```http
POST {{baseUrl}}/api/convenios/{{convenioId}}/company-documents/admin-upload
Authorization: Bearer {{tokenAdmin}}
Content-Type: multipart/form-data
```

### Body form-data

| Key | Type | Value |
|---|---|---|
| documentType | Text | RUT_O_RUNT |
| displayName | Text | RUT cargado manualmente |
| file | File | seleccionar PDF, JPG, JPEG o PNG |

### Response

Devuelve `CompanySubmittedDocumentResponse`.

```json
{
  "id": "uuid",
  "requestId": "uuid",
  "convenioId": "uuid",
  "documentType": "RUT_O_RUNT",
  "displayName": "RUT cargado manualmente",
  "originalFilename": "rut.pdf",
  "mimeType": "application/pdf",
  "fileSize": 450000,
  "status": "SUBIDO",
  "reviewComment": null,
  "uploadedAt": "2026-05-15T13:00:00",
  "approvedAt": null,
  "reviewedAt": null,
  "reviewedById": null,
  "reviewedByName": null,
  "replacedByDocumentId": null,
  "deletedFromStorageAt": null,
  "deletionReason": null
}
```

### Uso recomendado

Este endpoint es solo para migraciones, convenios históricos o excepciones administrativas. El flujo normal sigue siendo que la empresa externa suba sus documentos por token público.

---

# 10. Carga pública de documentos - empresa externa

## 10.1 Consultar información del token

```http
GET {{baseUrl}}/api/public/company-upload/{{uploadToken}}
```

### Body

No lleva body.

### Response

```json
{
  "requestId": "uuid",
  "convenioId": "uuid",
  "convenioCode": "CONV-2026-0001",
  "companyName": "Empresa Prueba Convenios SAS",
  "status": "PENDIENTE_EMPRESA",
  "roundNumber": 1,
  "expiresAt": "2026-05-22T12:30:00",
  "requiredDocuments": [
    {
      "documentType": "CEDULA_REPRESENTANTE",
      "displayName": "Cédula del representante legal"
    },
    {
      "documentType": "RUT_O_RUNT",
      "displayName": "RUT o RUNT"
    },
    {
      "documentType": "DOCUMENTO_ADICIONAL_1",
      "displayName": "Documento adicional 1"
    },
    {
      "documentType": "DOCUMENTO_ADICIONAL_2",
      "displayName": "Documento adicional 2"
    },
    {
      "documentType": "DOCUMENTO_ADICIONAL_3",
      "displayName": "Documento adicional 3"
    }
  ]
}
```

---

## 10.2 Subir documento por token público

```http
POST {{baseUrl}}/api/public/company-upload/{{uploadToken}}/documents
Content-Type: multipart/form-data
```

### Body form-data

| Key | Type | Value |
|---|---|---|
| documentType | Text | CEDULA_REPRESENTANTE |
| displayName | Text | Cédula representante legal |
| file | File | seleccionar PDF, JPG, JPEG o PNG |

Valores permitidos para `documentType`:

```text
CEDULA_REPRESENTANTE
RUT_O_RUNT
DOCUMENTO_ADICIONAL_1
DOCUMENTO_ADICIONAL_2
DOCUMENTO_ADICIONAL_3
```

### Response

```json
{
  "id": "uuid",
  "requestId": "uuid",
  "convenioId": "uuid",
  "documentType": "CEDULA_REPRESENTANTE",
  "displayName": "Cédula representante legal",
  "originalFilename": "cedula.pdf",
  "mimeType": "application/pdf",
  "fileSize": 450000,
  "status": "SUBIDO",
  "reviewComment": null,
  "uploadedAt": "2026-05-15T13:00:00",
  "approvedAt": null,
  "reviewedAt": null,
  "reviewedById": null,
  "reviewedByName": null,
  "replacedByDocumentId": null,
  "deletedFromStorageAt": null,
  "deletionReason": null
}
```

---

# 11. Aprobaciones

## 11.1 Mis aprobaciones pendientes

```http
GET {{baseUrl}}/api/approvals/my-pending
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "approvalRoundId": "uuid",
    "convenioId": "uuid",
    "convenioVersionId": "uuid",
    "convenioCode": "CONV-2026-0001",
    "convenioTitle": "Convenio de práctica empresarial",
    "stage": "PROYECCION",
    "stageOrder": 1,
    "assignedUserId": "uuid",
    "assignedUserEmail": "cristhian.orbes@campusucc.edu.co",
    "status": "PENDIENTE",
    "decisionComment": null,
    "assignedAt": "2026-05-15T14:00:00",
    "respondedAt": null,
    "approvalCode": null,
    "sealText": null
  }
]
```

---

## 11.2 Rondas de aprobación de un convenio

```http
GET {{baseUrl}}/api/approvals/convenios/{{convenioId}}/rounds
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "convenioId": "uuid",
    "convenioVersionId": "uuid",
    "roundNumber": 1,
    "status": "EN_PROCESO",
    "startedAt": "2026-05-15T14:00:00",
    "finishedAt": null
  }
]
```

---

## 11.3 Pasos de una ronda

```http
GET {{baseUrl}}/api/approvals/rounds/{{roundId}}/steps
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

Devuelve lista de `ApprovalStepResponse`.

---

## 11.4 Aprobar paso

```http
POST {{baseUrl}}/api/approvals/{{stepId}}/approve
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

```json
{
  "comment": "Aprobado por Proyección Social."
}
```

### Response

Devuelve `ApprovalStepResponse` con estado `APROBADO`.

---

## 11.5 Solicitar corrección

```http
POST {{baseUrl}}/api/approvals/{{stepId}}/request-correction
Authorization: Bearer {{tokenJuridico}}
```

### Body

```json
{
  "comment": "Se requiere corregir cláusula de obligaciones."
}
```

### Response

Devuelve `ApprovalStepResponse` con estado `CORRECCION_SOLICITADA`.

---

## 11.6 Rechazar paso

```http
POST {{baseUrl}}/api/approvals/{{stepId}}/reject
Authorization: Bearer {{tokenRectoria}}
```

### Body

```json
{
  "comment": "Convenio rechazado por incumplimiento de requisitos."
}
```

### Response

Devuelve `ApprovalStepResponse` con estado `RECHAZADO`.

---

# 12. Dashboard

## 12.1 Dashboard del usuario autenticado

```http
GET {{baseUrl}}/api/dashboard/me
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

```json
{
  "user": {
    "id": "uuid",
    "fullName": "Cristhian Orbes",
    "email": "cristhian.orbes@campusucc.edu.co",
    "roles": ["GESTOR_PROYECCION", "RECTOR_MEDELLIN"]
  },
  "reviewerProfiles": [
    {
      "id": "uuid",
      "roleName": "GESTOR_PROYECCION",
      "available": true,
      "currentActiveCases": 1,
      "maxActiveCases": 5,
      "sealName": "Gestor Proyección Social"
    }
  ],
  "summary": {
    "myPendingApprovals": 1,
    "myAlerts": 2,
    "myUnreadAlerts": 1,
    "myActiveCases": 1,
    "myCreatedConvenios": 0,
    "myConveniosInCorrection": 0,
    "myConveniosPendingCompanyDocuments": 0,
    "myConveniosReadyToSubmit": 0,
    "myPendingFormalizations": 1
  }
}
```

---

## 12.2 Trabajo pendiente del usuario

```http
GET {{baseUrl}}/api/dashboard/my-work
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

```json
{
  "pendingApprovals": [
    {
      "stepId": "uuid",
      "convenioId": "uuid",
      "convenioCode": "CONV-2026-0001",
      "convenioTitle": "Convenio de práctica empresarial",
      "companyName": "Empresa Prueba Convenios SAS",
      "stage": "PROYECCION",
      "assignedAt": "2026-05-15T14:00:00",
      "dueAt": "2026-05-18T14:00:00"
    }
  ],
  "alerts": [
    {
      "id": "uuid",
      "approvalStepId": "uuid",
      "convenioId": "uuid",
      "convenioCode": "CONV-2026-0001",
      "recipientUserId": "uuid",
      "recipientUserEmail": "cristhian.orbes@campusucc.edu.co",
      "alertType": "CONVENIO_PENDIENTE_FORMALIZACION",
      "audience": "PROYECCION_SOCIAL",
      "title": "Convenio pendiente de formalización",
      "message": "El convenio CONV-2026-0001 fue aprobado para firma y está pendiente de formalizar.",
      "createdAt": "2026-05-15T16:30:00",
      "readAt": null
    }
  ],
  "recentCreatedConvenios": [],
  "pendingFormalizations": [
    {
      "convenioId": "uuid",
      "convenioCode": "CONV-2026-0001",
      "companyName": "Empresa Prueba Convenios SAS",
      "status": "APROBADO_PARA_FIRMA",
      "stage": null,
      "convenioType": "PRACTICA",
      "createdAt": "2026-05-15T12:00:00",
      "updatedAt": "2026-05-15T16:30:00"
    }
  ]
}
```

---

## 12.3 Dashboard global ADMIN

```http
GET {{baseUrl}}/api/dashboard/admin/summary
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
{
  "totalUsers": 6,
  "activeUsers": 6,
  "totalCompanies": 3,
  "companiesPendingValidation": 1,
  "totalConvenios": 5,
  "conveniosByStatus": {
    "BORRADOR": 1,
    "PENDIENTE_DOCUMENTOS_EMPRESA": 1,
    "LISTO_PARA_RADICAR": 1,
    "EN_REVISION": 1,
    "APROBADO_PARA_FIRMA": 1,
    "FORMALIZADO": 0,
    "RECHAZADO": 0
  },
  "pendingApprovals": 2,
  "activeAlerts": 4,
  "conveniosApprovedForSignature": 1,
  "conveniosPendingFormalization": 1,
  "conveniosFormalized": 0
}
```

---

## 12.4 Actividad reciente

```http
GET {{baseUrl}}/api/dashboard/recent-activity
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "type": "CONVENIO_STATUS",
    "title": "Cambio de estado del convenio",
    "description": "Estado nuevo: EN_REVISION / Etapa: PROYECCION",
    "convenioId": "uuid",
    "convenioCode": "CONV-2026-0001",
    "performedBy": "Sebastian Ramos Toro",
    "createdAt": "2026-05-15T14:00:00"
  },
  {
    "type": "DOCUMENT_UPLOADED",
    "title": "Documento cargado por empresa",
    "description": "RUT o RUNT - rut.pdf",
    "convenioId": "uuid",
    "convenioCode": "CONV-2026-0001",
    "performedBy": "Empresa Prueba Convenios SAS",
    "createdAt": "2026-05-15T13:00:00"
  }
]
```

---

# 13. Alertas

## 13.1 Mis alertas

```http
GET {{baseUrl}}/api/review-alerts/me
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

```json
[
  {
    "id": "uuid",
    "approvalStepId": "uuid",
    "convenioId": "uuid",
    "convenioCode": "CONV-2026-0001",
    "recipientUserId": "uuid",
    "recipientUserEmail": "cristhian.orbes@campusucc.edu.co",
    "alertType": "CONVENIO_PENDIENTE_FORMALIZACION",
    "audience": "PROYECCION_SOCIAL",
    "title": "Convenio pendiente de formalización",
    "message": "El convenio CONV-2026-0001 fue aprobado para firma y está pendiente de formalizar.",
    "createdAt": "2026-05-15T16:30:00",
    "readAt": null
  }
]
```

---

## 13.2 Contador de alertas no leídas

```http
GET {{baseUrl}}/api/review-alerts/unread-count
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

```json
{
  "unreadCount": 3
}
```

---

## 13.3 Marcar alerta como leída

```http
POST {{baseUrl}}/api/review-alerts/{{alertId}}/read
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

Devuelve `ReviewAlertResponse` con `readAt` lleno.

---

## 13.4 Marcar todas mis alertas como leídas

```http
POST {{baseUrl}}/api/review-alerts/read-all
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

```json
{
  "unreadCount": 0
}
```

---

## 13.5 Alertas ADMIN

```http
GET {{baseUrl}}/api/review-alerts/admin
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

Lista de `ReviewAlertResponse`.

---

## 13.6 Alertas de Proyección Social

```http
GET {{baseUrl}}/api/review-alerts/proyeccion
Authorization: Bearer {{tokenGestorProyeccion}}
```

### Body

No lleva body.

### Response

Lista de `ReviewAlertResponse`.

---

## 13.7 Todas las alertas

```http
GET {{baseUrl}}/api/review-alerts
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

Lista de `ReviewAlertResponse`.

---

## 13.8 Ejecutar revisión de vencimientos

```http
POST {{baseUrl}}/api/review-alerts/check-deadlines
Authorization: Bearer {{tokenAdmin}}
```

### Body

No lleva body.

### Response

```text
Revisión de vencimientos ejecutada correctamente
```

---

# 14. Errores comunes

## 14.1 Error genérico

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Mensaje del error",
  "path": "/api/..."
}
```

## 14.2 Archivo demasiado pesado

```json
{
  "status": 413,
  "error": "Payload Too Large",
  "message": "El archivo supera el tamaño máximo permitido. El límite actual es de 25 MB por archivo.",
  "path": "/api/public/company-upload/.../documents"
}
```

## 14.3 Token inválido o vencido

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o vencido",
  "path": "/api/..."
}
```

---

# 15. Variables de entorno para despliegue

No incluir secretos reales en el repositorio ni en documentacion publica. Las claves reales deben quedar solo en Render o en el gestor seguro correspondiente.

```env
PORT=10000

DB_URL=jdbc:postgresql://HOST_AIVEN:PUERTO/defaultdb?sslmode=require
DB_USERNAME=USUARIO_AIVEN
DB_PASSWORD=PASSWORD_AIVEN

JWT_SECRET=CLAVE_LARGA_SEGURA_MINIMO_32_CARACTERES
JWT_EXPIRATION_MS=86400000

APP_PUBLIC_BASE_URL=https://TU-BACKEND.onrender.com
APP_SYSTEM_URL=https://c-loop.vercel.app
APP_COMPANY_UPLOAD_PATH=/api/public/company-upload
APP_CORS_ALLOWED_ORIGINS=https://c-loop.vercel.app,http://localhost:4200

APP_MAIL_ENABLED=true
MAIL_USERNAME=correo@gmail.com
MAIL_PASSWORD=app-password-gmail
MAIL_FROM=correo@gmail.com

APP_STORAGE_PROVIDER=supabase
APP_STORAGE_BASE_PATH=storage
SUPABASE_URL=https://PROJECT_REF.supabase.co
SUPABASE_BUCKET=convenios-documents
SUPABASE_SERVICE_ROLE_KEY=SOLO_EN_RENDER_NO_EN_ANGULAR
JPA_SHOW_SQL=false
```

Notas:

- `APP_PUBLIC_BASE_URL` debe ser la URL publica del backend en Render. Se usa para construir enlaces publicos, por ejemplo carga documental por token.
- `APP_SYSTEM_URL` debe ser la URL del frontend. En este caso: `https://c-loop.vercel.app`.
- Angular no debe conectarse directamente a Aiven ni a Supabase. Angular debe consumir solo el backend Spring Boot.
- `SUPABASE_SERVICE_ROLE_KEY` no debe ir en Angular, README publico ni repositorio. Solo debe configurarse como variable segura en Render.
- Si se prueba localmente sin Supabase, se puede usar `APP_STORAGE_PROVIDER=local` y `APP_STORAGE_BASE_PATH=storage`.

---

