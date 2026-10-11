package org.pacientes.bean.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("paciente")
public class Paciente {

	@Id
	private Long id;

	@Column("tipo_documento")
	private String tipoDocumento;

	@Column("numero_documento")
	private String numeroDocumento;

	private String nombres;
	private String apellidos;

	@Column("fecha_nacimiento")
	private LocalDate fechaNacimiento;

	private String telefono;
	private String correo;
	private String direccion;

	@Column("fecha_registro")
	private LocalDate fechaRegistro;

	private String estado;

}
