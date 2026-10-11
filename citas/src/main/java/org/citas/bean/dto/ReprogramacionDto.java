package org.citas.bean.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReprogramacionDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalDateTime fechaHora;

}
