# Guía de evidencias (§29 C)

Lista de capturas que pide el examen, dónde tomarlas y qué debe verse en cada una. Guardar las imágenes en `docs/evidencias/` con el nombre indicado.

Estado: **Lista** = la ejecución ya existe y solo falta la captura · **Pendiente** = hay que ejecutar el sistema completo (MySQL y RabbitMQ) y capturar.

## 1. Eureka y Gateway

| Archivo | Qué capturar | Qué debe verse | Estado |
|---|---|---|---|
| 01-eureka.png | http://localhost:8761 con todo arrancado | 7 instancias: GATEWAY, PACIENTES, CITAS, ATENCIONES, NOTIFICACIONES, AUDITORIA, PAGOS | Pendiente |
| 02-gateway.png | Postman: `GET http://localhost:8080/api/pacientes/1` | Respuesta 200 obtenida a través del puerto 8080 | Pendiente |

## 2. Endpoints y reglas de negocio (colección Postman)

| Archivo | Petición de la colección | Respuesta esperada | Estado |
|---|---|---|---|
| 03-paciente-registrado.png | 1 · Registrar paciente 1 | 201 | Pendiente |
| 04-paciente-duplicado.png | 1 · ERROR - Paciente duplicado | 409 | Pendiente |
| 05-paciente-por-id.png | 2 · Buscar paciente por id | 200 | Pendiente |
| 06-cita-registrada.png | 3 · Registrar cita 1 | 201, estado PENDIENTE | Pendiente |
| 07-sincrona-paciente-inexistente.png | 3 · ERROR - Paciente inexistente | 400 "El paciente 999 no existe" (respuesta de WebClient) | Pendiente |
| 08-cita-confirmada.png | 4 · Confirmar cita 1 | 200, estado CONFIRMADA | Pendiente |
| 09-transicion-no-permitida.png | 4 · ERROR - Confirmar cita cancelada | 409 | Pendiente |
| 10-atencion-registrada.png | 5 · Registrar atención de la cita 1 | 201, estado REGISTRADA | Pendiente |
| 11-pago-monto-invalido.png | 6 · ERROR - Monto cero | 400 | Pendiente |
| 12-runner-postman.png | Collection Runner con toda la colección | Las 48 peticiones con sus códigos | Pendiente |

## 3. RabbitMQ: evento publicado y consumido

| Archivo | Qué capturar | Qué debe verse | Estado |
|---|---|---|---|
| 13-rabbitmq-colas.png | http://localhost:15672 → Queues | NOTIFICACIONES_COLA y AUDITORIA_COLA con 1 consumidor cada una | Pendiente |
| 14-rabbitmq-exchange.png | Exchanges → HOSPITAL_INTERCAMBIO → Bindings | Las 2 colas enlazadas con CITA_KEY | Pendiente |
| 15-consola-citas.png | Consola de citas al confirmar | `Envio: CitaEventoDto(evento=CitaConfirmada, ...)` | Pendiente |
| 16-consola-notificaciones.png | Consola de notificaciones | `Notificacion enviada al paciente: [CitaConfirmada] ...` | Pendiente |
| 17-notificaciones-api.png | 7 · Ver notificaciones recibidas | Lista de avisos | Pendiente |
| 18-auditoria-api.png | 7 · Historial de la cita 1 | CitaRegistrada → CitaConfirmada → CitaAtendida | Pendiente |

## 4. Persistencia SQL con R2DBC

| Archivo | Qué capturar | Estado |
|---|---|---|
| 19-mysql-citas.png | `SELECT * FROM bd_citas.cita;` después de ejecutar la colección | Pendiente |
| 20-mysql-auditoria.png | `SELECT * FROM bd_auditoria.auditoria;` | Pendiente |

## 5. Pruebas automatizadas

| Archivo | Qué capturar | Estado |
|---|---|---|
| 21-pruebas-locales.png | En `pacientes`: `mvnw.cmd verify` con `Tests run: 6, Failures: 0` y `BUILD SUCCESS` | Pendiente (ya verificado al desarrollar; falta la captura en tu equipo) |

## 6. GitHub Actions

| Archivo | Qué capturar | Estado |
|---|---|---|
| 22-ci-verde.png | [Run 38103043089](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/runs/38103043089): los 8 jobs en verde | Lista |
| 23-ci-log-pacientes.png | Log de build-test (pacientes) de esa ejecución: `Tests run: 6` y `BUILD SUCCESS` | Lista |
| 24-ci-fallo-pr.png | PR #1 con "1 failing, 7 successful checks" | Lista (ya tomada) |
| 25-ci-fallo-log.png | [Run 38103389478](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/runs/38103389478), job build-test (pacientes): `Status expected:<409 CONFLICT> but was:<400 BAD_REQUEST>` y `BUILD FAILURE` | Lista |
| 26-ci-correccion.png | PR #1 con "All checks have passed" y los commits 68bba09 (✗) y a5e0fd2 (✓) | Lista (ya tomada) |
| 27-ci-merge-main.png | [Run 38103640315](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/runs/38103640315): merge en main en verde | Lista |

## 7. Diagramas

Ya están en `docs/diagramas/` y dentro del documento técnico: arquitectura, comunicación del flujo funcional, estados y CI.
