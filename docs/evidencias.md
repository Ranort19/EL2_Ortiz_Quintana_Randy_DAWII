# Evidencias (§29 C)

Las capturas de funcionamiento se incluyen en el **Anexo: Evidencias** de la versión Word del documento técnico (`docs/Documento_Tecnico_Hospital_Vida_Salud.docx`). Esta lista indica qué muestra cada evidencia y qué requisito del examen cubre.

| Anexo | Evidencia | Qué muestra | Requisito |
|---|---|---|---|
| A.1 | Eureka con los servicios registrados | Panel http://localhost:8761 con GATEWAY, PACIENTES, CITAS, ATENCIONES, NOTIFICACIONES, AUDITORIA y PAGOS | §18 |
| A.2 | Enrutamiento por el Gateway | Peticiones a `http://localhost:8080/api/...` respondidas por cada servicio | §17 |
| A.3 | Endpoints y reglas de negocio | Registro y consulta de paciente, rechazo de duplicado (409), registro de cita, validación síncrona del paciente, transición no permitida (409), atención y pago con monto inválido (400) | §28 |
| A.4 | Persistencia SQL con R2DBC | Datos guardados en `bd_citas`, `bd_auditoria` y demás bases después de ejecutar la colección | §21 |
| A.5 | RabbitMQ | Colas `NOTIFICACIONES_COLA` y `AUDITORIA_COLA` con su consumidor, publicación en citas y consumo en notificaciones y auditoria | §20 |
| A.6 | Pruebas automatizadas | `mvnw verify` con las pruebas en verde | §24 |
| A.7 | GitHub Actions: ejecución exitosa | [Run 38103043089](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/runs/38103043089) con los 8 jobs en verde | §25 |
| A.8 | Fallo controlado y corrección | PR #1: [run 38103389478](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/runs/38103389478) en rojo (`RechazarDocumentoDuplicado`), [run 38103506117](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/runs/38103506117) en verde tras la corrección y [run 38103640315](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/runs/38103640315) del merge en main | §25.3 |

Los diagramas de arquitectura y del flujo funcional están en `docs/diagramas/` y en la sección 4 del documento técnico.
