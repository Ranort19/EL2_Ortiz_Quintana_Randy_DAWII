package org.atenciones.bean.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String codigo;
	private Long pacienteId;
	private String especialidad;
	private LocalDateTime fechaHora;
	private String estado;

}
