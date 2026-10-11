package org.citas.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

	public static final String EXCHANGE = "HOSPITAL_INTERCAMBIO";
	public static final String ROUTING_KEY = "CITA_KEY";
	public static final String QUEUE_NOTIFICACIONES = "NOTIFICACIONES_COLA";
	public static final String QUEUE_AUDITORIA = "AUDITORIA_COLA";

	@Bean
	Queue notificacionesQueue() {
		return new Queue(QUEUE_NOTIFICACIONES, true);
	}

	@Bean
	Queue auditoriaQueue() {
		return new Queue(QUEUE_AUDITORIA, true);
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	Binding notificacionesBinding() {
		return BindingBuilder
				.bind(notificacionesQueue())
				.to(exchange())
				.with(ROUTING_KEY);
	}

	@Bean
	Binding auditoriaBinding() {
		return BindingBuilder
				.bind(auditoriaQueue())
				.to(exchange())
				.with(ROUTING_KEY);
	}

	@Bean
	MessageConverter messageConverter() {
		return new JacksonJsonMessageConverter();
	}

}
