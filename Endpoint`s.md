# 4. Endpoints existentes

## 4.1 Autenticacion

| Metodo | Endpoint | Auth | Uso |
|---|---|---|---|
| POST | `/api/auth/register-code/request` | No | Envia codigo de verificacion al correo institucional. |
| POST | `/api/auth/register` | No | Registra usuario usando codigo de 6 digitos. |
| POST | `/api/auth/login` | No | Inicia sesion y devuelve JWT. |

### Body - pedir codigo

```json
{
  "email": "sebastian.ramostoro@campusucc.edu.co"
}
```

### Body - registro

```json
{
  "fullName": "Sebastian Ramos Toro",
  "email": "sebastian.ramostoro@campusucc.edu.co",
  "password": "ConveniosUCC2026*",
  "code": "123456"
}
```

### Body - login

```json
{
  "email": "sebastian.ramostoro@campusucc.edu.co",
  "password": "ConveniosUCC2026*"
}
```

---

## 4.2 Usuarios

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| GET | `/api/users/me` | Usuario autenticado | Devuelve el usuario logueado y sus roles. |
| GET | `/api/users` | ADMIN | Lista usuarios registrados. |

---

## 4.3 Roles

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| GET | `/api/roles` | ADMIN | Lista roles disponibles. |
| POST | `/api/roles/assign` | ADMIN | Asigna un rol a un usuario. Si es rol revisor, crea `ReviewerProfile`. |
| POST | `/api/roles/remove` | ADMIN | Quita rol a usuario. No debe quitar ADMIN desde este endpoint. |

### Body asignar/quitar rol

```json
{
  "email": "cristhian.orbes@campusucc.edu.co",
  "roleName": "GESTOR_PROYECCION"
}
```

---

## 4.4 Empresas

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| POST | `/api/companies` | Usuario autenticado | Crea empresa en borrador. |
| GET | `/api/companies` | Usuario autenticado | Lista empresas. |
| GET | `/api/companies/{id}` | Usuario autenticado | Consulta empresa por ID. |
| GET | `/api/companies/by-nit/{nit}` | Usuario autenticado | Consulta empresa por NIT. |
| GET | `/api/companies/pending-validation` | ADMIN o REVISOR_JURIDICO | Lista empresas pendientes de validacion. |
| POST | `/api/companies/{id}/submit-validation` | Usuario autenticado | Envia empresa a validacion. |
| POST | `/api/companies/{id}/validate` | ADMIN o REVISOR_JURIDICO | Valida empresa. |
| POST | `/api/companies/{id}/observe` | ADMIN o REVISOR_JURIDICO | Observa empresa. |
| POST | `/api/companies/{id}/reject` | ADMIN o REVISOR_JURIDICO | Rechaza empresa. |
| GET | `/api/companies/{id}/history` | Usuario autenticado | Historial de validacion de empresa. |

### Body crear empresa

```json
{
  "nit": "900123456-1",
  "businessName": "Empresa Prueba Convenios SAS",
  "tradeName": "Empresa Prueba",
  "identificationType": "NIT",
  "legalRepresentativeName": "Representante Legal Prueba",
  "contactEmail": "sebas-ramos2018@outlook.com",
  "contactPhone": "3001234567",
  "address": "Calle 123 #45-67"
}
```

### Body validar/observar/rechazar empresa

```json
{
  "comment": "Empresa validada para prueba local."
}
```

---

## 4.5 Convenios

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| POST | `/api/convenios` | Usuario autenticado | Crea convenio. |
| GET | `/api/convenios` | Usuario autenticado | Lista convenios. |
| GET | `/api/convenios/{id}` | Usuario autenticado | Consulta detalle de convenio. |
| GET | `/api/convenios/{id}/versions` | Usuario autenticado | Lista versiones del convenio. |
| GET | `/api/convenios/{id}/history` | Usuario autenticado | Historial de estados del convenio. |
| POST | `/api/convenios/{id}/submit` | Usuario autenticado | Radica convenio y crea primera etapa de aprobacion. |
| POST | `/api/convenios/{id}/preview-pdf` | Usuario autenticado | Genera PDF preliminar. |
| GET | `/api/convenios/{id}/preview-pdf` | Usuario autenticado | Descarga/visualiza PDF preliminar. |
| GET | `/api/convenios/{convenioId}/versions/{versionId}/pdf` | Usuario autenticado | Descarga PDF oficial de version. |
| GET | `/api/convenios/{id}/documents` | Usuario autenticado | Lista documentos generados del convenio. |
| GET | `/api/convenios/{convenioId}/documents/{documentId}/pdf` | Usuario autenticado | Descarga documento generado. |

### Body crear convenio

```json
{
  "companyId": "{{companyId}}",
  "convenioType": "PRACTICA",
  "title": "Convenio de practicas profesionales",
  "objective": "Permitir el desarrollo de practicas profesionales de estudiantes.",
  "description": "Convenio creado para prueba local del flujo documental y aprobacion institucional.",
  "durationMonths": 12,
  "startDate": "2026-05-08",
  "endDate": "2027-05-08",
  "externalEntityObligations": "Recibir estudiantes practicantes y acompanar su proceso formativo.",
  "universityObligations": "Gestionar, revisar y hacer seguimiento al convenio.",
  "estimatedValue": 0
}
```

---

## 4.6 Documentos de empresa - flujo interno

Base: `/api/convenios/{convenioId}`

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| POST | `/request-company-documents` | Usuario autenticado | Envia correo a empresa con link publico de carga. |
| GET | `/company-document-requests` | Usuario autenticado | Lista solicitudes documentales. |
| GET | `/company-documents` | Usuario autenticado | Lista documentos cargados por la empresa. |
| POST | `/company-documents/{documentId}/approve` | Usuario autenticado | Aprueba documento cargado. |
| POST | `/company-documents/{documentId}/observe` | Usuario autenticado | Observa documento y opcionalmente elimina archivo fisico. |
| POST | `/company-documents/request-correction` | Usuario autenticado | Solicita correccion documental temprana a la empresa. |
| POST | `/company-documents/discard` | Usuario autenticado | Descarta el proceso documental temprano. |
| POST | `/mark-documents-approved` | Usuario autenticado | Marca documentos como aprobados y deja convenio listo para radicar. |

### Body observar documento

```json
{
  "comment": "Documento ilegible, por favor cargar una version clara.",
  "deletePhysicalFile": true
}
```

### Body solicitar correccion documental

```json
{
  "comment": "Falta actualizar el RUT y cargar documento adicional."
}
```

### Body descartar proceso documental

```json
{
  "comment": "La empresa no completo los documentos requeridos."
}
```

---

## 4.7 Carga publica de documentos por empresa

Estos endpoints no usan JWT. Usan token enviado por correo al `contactEmail` de la empresa.

| Metodo | Endpoint | Auth | Uso |
|---|---|---|---|
| GET | `/api/public/company-upload/{token}` | Token publico | Muestra informacion publica de carga. |
| POST | `/api/public/company-upload/{token}/documents` | Token publico | Sube documento con `multipart/form-data`. |

### Form-data subir documento

| Key | Type | Value |
|---|---|---|
| documentType | Text | `CEDULA_REPRESENTANTE` |
| displayName | Text | `Cedula representante legal` |
| file | File | Seleccionar PDF o imagen |

Los documentos se guardan localmente en:

```text
storage/convenios/company-documents/{convenioId}/{requestId}/{uuid-nombreArchivo}
```
## 4.8 Aprobaciones

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| GET | `/api/approvals/my-pending` | Usuario autenticado | Lista etapas pendientes asignadas al usuario. |
| GET | `/api/approvals/convenios/{convenioId}/rounds` | Usuario autenticado | Lista rondas de aprobacion de un convenio. |
| GET | `/api/approvals/rounds/{roundId}/steps` | Usuario autenticado | Lista pasos de una ronda. |
| POST | `/api/approvals/{stepId}/approve` | Usuario asignado | Aprueba etapa. |
| POST | `/api/approvals/{stepId}/request-correction` | Usuario asignado | Solicita correccion. |
| POST | `/api/approvals/{stepId}/reject` | Usuario asignado | Rechaza etapa. |

### Body aprobar/corregir/rechazar

```json
{
  "comment": "Aprobado por Proyeccion Social."
}
```

---

## 4.9 Alertas existentes

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| GET | `/api/review-alerts/me` | Usuario autenticado | Lista alertas del usuario. |
| GET | `/api/review-alerts/admin` | ADMIN | Lista alertas administrativas. |
| GET | `/api/review-alerts/proyeccion` | ADMIN o GESTOR_PROYECCION | Lista alertas de Proyeccion Social. |
| GET | `/api/review-alerts` | ADMIN | Lista todas las alertas. |
| POST | `/api/review-alerts/check-deadlines` | ADMIN | Ejecuta revision de vencimientos. |

---

# 5. Endpoints nuevos recomendados para dashboard y alertas

## 5.1 Dashboard

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| GET | `/api/dashboard/me` | Usuario autenticado | Resumen del usuario, roles, perfiles revisores y contadores. |
| GET | `/api/dashboard/my-work` | Usuario autenticado | Pendientes, alertas y convenios recientes del usuario. |
| GET | `/api/dashboard/admin/summary` | ADMIN | Resumen global del sistema para tarjetas de admin. |
| GET | `/api/dashboard/recent-activity` | Usuario autenticado | Actividad reciente del sistema. |

### Respuesta esperada - `/api/dashboard/me`

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
      "sealName": null
    }
  ],
  "summary": {
    "myPendingApprovals": 1,
    "myAlerts": 2,
    "myUnreadAlerts": 2,
    "myActiveCases": 1,
    "myCreatedConvenios": 0,
    "myConveniosInCorrection": 0,
    "myConveniosPendingCompanyDocuments": 0,
    "myConveniosReadyToSubmit": 0
  }
}
```

## 5.2 Alertas nuevas

| Metodo | Endpoint | Auth/Rol | Uso |
|---|---|---|---|
| GET | `/api/review-alerts/unread-count` | Usuario autenticado | Devuelve contador de alertas no leidas. |
| POST | `/api/review-alerts/{alertId}/read` | Usuario autenticado | Marca una alerta propia como leida. |
| POST | `/api/review-alerts/read-all` | Usuario autenticado | Marca todas las alertas propias como leidas. |

### Respuesta contador

```json
{
  "unreadCount": 3
}
```

---
