CREATE DATABASE IF NOT EXISTS bd_pacientes;
USE bd_pacientes;

CREATE TABLE IF NOT EXISTS paciente (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento    VARCHAR(10)  NOT NULL,
    numero_documento  VARCHAR(15)  NOT NULL,
    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    fecha_nacimiento  DATE         NOT NULL,
    telefono          VARCHAR(15)  NOT NULL,
    correo            VARCHAR(100) NOT NULL,
    direccion         VARCHAR(150),
    fecha_registro    DATE         NOT NULL,
    estado            VARCHAR(10)  NOT NULL,
    CONSTRAINT uk_paciente_documento UNIQUE (tipo_documento, numero_documento)
);

CREATE DATABASE IF NOT EXISTS bd_citas;
USE bd_citas;

CREATE TABLE IF NOT EXISTS cita (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo               VARCHAR(20)  NOT NULL UNIQUE,
    paciente_id          BIGINT       NOT NULL,
    especialidad         VARCHAR(30)  NOT NULL,
    fecha_hora           DATETIME     NOT NULL,
    profesional_codigo   VARCHAR(20)  NOT NULL,
    motivo               VARCHAR(150) NOT NULL,
    estado               VARCHAR(15)  NOT NULL,
    fecha_creacion       DATETIME     NOT NULL,
    fecha_actualizacion  DATETIME     NOT NULL,
    INDEX idx_cita_paciente (paciente_id),
    INDEX idx_cita_agenda (profesional_codigo, fecha_hora)
);

CREATE DATABASE IF NOT EXISTS bd_atenciones;
USE bd_atenciones;

CREATE TABLE IF NOT EXISTS atencion (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20) NOT NULL UNIQUE,
    paciente_id     BIGINT      NOT NULL,
    cita_id         BIGINT      NOT NULL UNIQUE,
    fecha_atencion  DATETIME    NOT NULL,
    area            VARCHAR(30) NOT NULL,
    estado          VARCHAR(15) NOT NULL,
    INDEX idx_atencion_paciente (paciente_id)
);

CREATE DATABASE IF NOT EXISTS bd_auditoria;
USE bd_auditoria;

CREATE TABLE IF NOT EXISTS auditoria (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha            DATETIME    NOT NULL,
    recurso          VARCHAR(20) NOT NULL,
    recurso_id       BIGINT      NOT NULL,
    evento           VARCHAR(30) NOT NULL,
    estado_anterior  VARCHAR(15),
    estado_nuevo     VARCHAR(15) NOT NULL,
    origen           VARCHAR(30) NOT NULL,
    INDEX idx_auditoria_recurso (recurso, recurso_id)
);

CREATE DATABASE IF NOT EXISTS bd_pagos;
USE bd_pagos;

CREATE TABLE IF NOT EXISTS pago (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id       BIGINT       NOT NULL,
    concepto          VARCHAR(100) NOT NULL,
    monto             DOUBLE       NOT NULL CHECK (monto > 0),
    fecha_pago        DATE         NOT NULL,
    medio_pago        VARCHAR(20)  NOT NULL,
    numero_operacion  VARCHAR(30)  NOT NULL UNIQUE,
    estado            VARCHAR(15)  NOT NULL,
    INDEX idx_pago_paciente (paciente_id)
);
