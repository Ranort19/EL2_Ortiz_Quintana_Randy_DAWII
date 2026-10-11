package org.pagos.bean.entity;

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
@Table("pago")
public class Pago {

	@Id
	private Long id;

	@Column("paciente_id")
	private Long pacienteId;

	private String concepto;
	private Double monto;

	@Column("fecha_pago")
	private LocalDate fechaPago;

	@Column("medio_pago")
	private String medioPago;

	@Column("numero_operacion")
	private String numeroOperacion;

	private String estado;

}
