package org.citas.bean.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String tipoDocumento;
	private String numeroDocumento;
	private String nombres;
	private String apellidos;
	private String estado;

}
