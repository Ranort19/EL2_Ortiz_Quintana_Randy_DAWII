package org.citas.model;

import org.citas.bean.dto.CitaEventoDto;
import org.citas.configuration.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Service
public class RabbitMQModel {

	private final RabbitTemplate rabbitTemplate;

	public Mono<Void> Publicar(CitaEventoDto evento) {
		return Mono.fromRunnable(() -> {
					rabbitTemplate.convertAndSend(
							RabbitMQConfiguration.EXCHANGE,
							RabbitMQConfiguration.ROUTING_KEY,
							evento
					);
					System.out.println("Envio: " + evento);
				})
				.subscribeOn(Schedulers.boundedElastic())
				.onErrorResume(error -> {
					System.out.println("No se pudo publicar el evento " + evento.getEvento() + ": " + error.getMessage());
					return Mono.empty();
				})
				.then();
	}

}
