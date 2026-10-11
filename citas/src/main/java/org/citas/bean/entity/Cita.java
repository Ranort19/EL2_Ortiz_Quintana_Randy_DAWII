package org.citas.bean.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("cita")
public class Cita {

	@Id
	private Long id;

	private String codigo;

	@Column("paciente_id")
	private Long pacienteId;

	private String especialidad;

	@Column("fecha_hora")
	private LocalDateTime fechaHora;

	@Column("profesional_codigo")
	private String profesionalCodigo;

	private String motivo;
	private String estado;

	@Column("fecha_creacion")
	private LocalDateTime fechaCreacion;

	@Column("fecha_actualizacion")
	private LocalDateTime fechaActualizacion;

}
