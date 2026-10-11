package org.auditoria.bean.entity;

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
@Table("auditoria")
public class Auditoria {

	@Id
	private Long id;

	private LocalDateTime fecha;
	private String recurso;

	@Column("recurso_id")
	private Long recursoId;

	private String evento;

	@Column("estado_anterior")
	private String estadoAnterior;

	@Column("estado_nuevo")
	private String estadoNuevo;

	private String origen;

}
