package org.notificaciones.configuration;

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
	public static final String QUEUE = "NOTIFICACIONES_COLA";
	public static final String ROUTING_KEY = "CITA_KEY";

	@Bean
	Queue queue() {
		return new Queue(QUEUE, true);
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder
				.bind(queue)
				.to(exchange)
				.with(ROUTING_KEY);
	}

	@Bean
	MessageConverter messageConverter() {
		return new JacksonJsonMessageConverter();
	}

}
