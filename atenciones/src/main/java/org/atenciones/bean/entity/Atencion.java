package org.atenciones.bean.entity;

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
@Table("atencion")
public class Atencion {

	@Id
	private Long id;

	private String codigo;

	@Column("paciente_id")
	private Long pacienteId;

	@Column("cita_id")
	private Long citaId;

	@Column("fecha_atencion")
	private LocalDateTime fechaAtencion;

	private String area;
	private String estado;

}
