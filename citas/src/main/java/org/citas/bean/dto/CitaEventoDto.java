package org.citas.bean.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaEventoDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String evento;
	private Long citaId;
	private String codigo;
	private Long pacienteId;
	private LocalDateTime fechaHora;
	private String estadoAnterior;
	private String estadoNuevo;
	private LocalDateTime fecha;
	private String origen;

}
