package org.auditoria.component;

import org.auditoria.bean.dto.CitaEventoDto;
import org.auditoria.configuration.RabbitMQConfiguration;
import org.auditoria.model.AuditoriaModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ConsumidorComponent {

	private final AuditoriaModel auditoriaModel;

	@RabbitListener(queues = RabbitMQConfiguration.QUEUE)
	public void Procesar(CitaEventoDto evento) {
		auditoriaModel.Registrar(evento)
				.subscribe(
						registro -> System.out.println("Auditoria registrada: " + registro),
						error -> System.out.println("No se pudo registrar la auditoria del evento "
								+ evento.getEvento() + ": " + error.getMessage()));
	}

}
