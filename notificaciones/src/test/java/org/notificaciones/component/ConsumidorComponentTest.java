package org.notificaciones.component;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.notificaciones.bean.dto.CitaEventoDto;
import org.notificaciones.model.NotificacionModel;

import reactor.test.StepVerifier;

class ConsumidorComponentTest {

	private final NotificacionModel notificacionModel = new NotificacionModel();

	private final ConsumidorComponent consumidorComponent = new ConsumidorComponent(notificacionModel);

	@Test
	void ProcesarEventoCitaConfirmada() {
		CitaEventoDto evento = new CitaEventoDto("CitaConfirmada", 1L, "CIT-1", 1L,
				LocalDateTime.of(2027, 1, 15, 9, 30), "PENDIENTE", "CONFIRMADA", LocalDateTime.now(), "citas");

		consumidorComponent.Procesar(evento);

		StepVerifier.create(notificacionModel.ListarNotificaciones())
				.expectNextMatches(notificaciones -> notificaciones.size() == 1
						&& notificaciones.get(0).contains("[CitaConfirmada] Paciente 1 - Cita CIT-1")
						&& notificaciones.get(0).contains("ha sido confirmada"))
				.verifyComplete();
	}

}
