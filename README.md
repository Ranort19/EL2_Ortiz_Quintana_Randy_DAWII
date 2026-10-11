# Hospital Vida Salud · Microservicios (T2)

[![Integracion Continua](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/workflows/ci.yml/badge.svg)](https://github.com/Ranort19/EL2_Ortiz_Quintana_Randy_DAWII/actions/workflows/ci.yml)

Segunda Evaluación T2 del curso Desarrollo de Aplicaciones Web II (Cibertec). Plataforma administrativa de un hospital construida con microservicios: registro de pacientes, programación de citas, atención simulada, pagos simulados, notificaciones y auditoría.

> Sistema académico. No se usa para diagnosticar ni para tomar decisiones clínicas. Todos los datos de prueba son ficticios.

## Arquitectura

![Arquitectura](docs/diagramas/arquitectura.jpg)

| Servicio | Puerto | Responsabilidad | Base de datos |
|---|---|---|---|
| eureka-server | 8761 | Registro y descubrimiento de servicios | — |
| gateway | 8080 | Punto de entrada único (`/api/**`) | — |
| pacientes | 8081 | Registro, consulta, actualización y validación de duplicados | bd_pacientes |
| citas | 8082 | Programación, reprogramación, estados y publicación de eventos | bd_citas |
| atenciones | 8083 | Atención administrativa vinculada a una cita confirmada | bd_atenciones |
| notificaciones | 8084 | Consume eventos de citas y genera avisos (log y memoria) | — |
| auditoria | 8085 | Consume eventos de citas y guarda la trazabilidad | bd_auditoria |
| pagos | 8086 | Pagos simulados con monto válido y operación única | bd_pagos |

- **Comunicación síncrona:** WebClient con `@LoadBalanced` y Eureka. citas → pacientes, atenciones → citas y pagos → pacientes, con tiempo de espera de 3 s.
- **Comunicación asíncrona:** citas publica `CitaEventoDto` en el exchange `HOSPITAL_INTERCAMBIO` con la routing key `CITA_KEY`. Las colas `NOTIFICACIONES_COLA` y `AUDITORIA_COLA` lo entregan a notificaciones y auditoria.
- **Persistencia:** MySQL con Spring Data R2DBC, una base por servicio, sin claves foráneas entre bases.

El detalle completo está en el [documento técnico](docs/documento-tecnico.md).

## Tecnologías

Java 21 · Spring Boot 4.0.6 · Spring Cloud 2025.1.0 · Spring WebFlux · Spring Data R2DBC (MySQL) · Spring Cloud Gateway · Eureka · RabbitMQ · Lombok · JUnit 5 · Mockito · Maven Wrapper · GitHub Actions

## Requisitos

- Java 21
- MySQL 8.0.16 o superior
- RabbitMQ con el plugin de administración (http://localhost:15672)
- Postman, para las pruebas funcionales

No hace falta instalar Maven: cada proyecto incluye Maven Wrapper (`mvnw` / `mvnw.cmd`).

## Configuración

Las credenciales no están en el repositorio. Se leen de variables de entorno:

| Variable | Usada por | Valor por defecto |
|---|---|---|
| `DB_USERNAME` | pacientes, citas, atenciones, auditoria, pagos | `root` |
| `DB_PASSWORD` | pacientes, citas, atenciones, auditoria, pagos | vacío |
| `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | citas, notificaciones, auditoria | `guest` / `guest` |

En PowerShell, antes de arrancar un servicio:

```powershell
$env:DB_PASSWORD = "tu_clave"
```

## Ejecución

1. Crear las bases de datos ejecutando [script/hospital.sql](script/hospital.sql) en MySQL.
2. Iniciar RabbitMQ.
3. Arrancar cada proyecto desde su carpeta, en este orden: **eureka-server**, **gateway**, y después pacientes, citas, atenciones, notificaciones, auditoria y pagos.

```bash
cd eureka-server
./mvnw spring-boot:run
```

En Windows (CMD o PowerShell) usar `mvnw.cmd spring-boot:run`.

4. Verificar en http://localhost:8761 que aparecen los 7 servicios registrados.

## Pruebas

### Pruebas automatizadas

```bash
cd pacientes
./mvnw verify
```

No necesitan MySQL, RabbitMQ ni Eureka.

| Proyecto | Clase de prueba | Pruebas |
|---|---|---|
| pacientes | `PacienteControllerTest` (controlador, foco del CI) | 6 |
| citas | `CitaModelTest` | 4 |
| notificaciones | `ConsumidorComponentTest` | 1 |
| pagos | `PagoModelTest` | 4 |

### Pruebas funcionales con Postman

Importar [postman/Hospital_Vida_Salud.json](postman/Hospital_Vida_Salud.json) y ejecutar la colección en orden sobre bases recién creadas. Tiene 48 peticiones con casos correctos y de error: pacientes, consultas, citas, estados y eventos, atenciones, pagos, notificaciones y auditoría. Todas pasan por el gateway (`http://localhost:8080`).

## Integración continua

El workflow [.github/workflows/ci.yml](.github/workflows/ci.yml) se ejecuta en cada push y pull request hacia `main`. Usa una matriz con un job por proyecto (8 jobs): configura Java 21 con caché de Maven y ejecuta `./mvnw --batch-mode verify`. Si la compilación o una prueba falla, el job queda en rojo.

![Integración continua](docs/diagramas/ci.jpg)

## Estructura del repositorio

```
.github/workflows/ci.yml   Workflow de GitHub Actions
eureka-server/             Servidor Eureka
gateway/                   Spring Cloud Gateway
pacientes/ citas/ atenciones/ notificaciones/ auditoria/ pagos/
script/hospital.sql        Bases de datos y tablas
postman/                   Colección de pruebas funcionales
docs/                      Documento técnico, diagramas y lista de evidencias
```

Cada microservicio sigue la estructura del primer examen: `bean/entity`, `bean/dto`, `controller`, `model`, `repository`, `configuration`, `usecase` y `component`.

## Documentación

- [Documento técnico](docs/documento-tecnico.md)
- [Lista de evidencias](docs/evidencias.md)
- [Diagramas](docs/diagramas)

**Autor:** Ortiz Quintana, Randy · Curso 4698 Desarrollo de Aplicaciones Web II · Profesor Zambrano Macedo, Yuri Renzo
