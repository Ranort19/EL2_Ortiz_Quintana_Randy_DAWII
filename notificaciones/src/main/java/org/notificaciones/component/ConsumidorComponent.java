package org.notificaciones.component;

import org.notificaciones.bean.dto.CitaEventoDto;
import org.notificaciones.configuration.RabbitMQConfiguration;
import org.notificaciones.model.NotificacionModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ConsumidorComponent {

	private final NotificacionModel notificacionModel;

	@RabbitListener(queues = RabbitMQConfiguration.QUEUE)
	public void Procesar(CitaEventoDto evento) {
		String mensaje = notificacionModel.Guardar(evento);
		System.out.println("Notificacion enviada al paciente: " + mensaje);
	}

}
