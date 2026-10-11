package org.atenciones.usecase;

import java.time.Duration;

import org.atenciones.bean.dto.CitaDto;
import org.atenciones.bean.dto.EstadoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class CitaUseCase {

	String http = "http://citas";

	private final WebClient.Builder webClientBuilder;

	public Mono<CitaDto> BuscarCita(Long id) {
		return webClientBuilder.build()
				.get()
				.uri(http + "/citas/{id}", id)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, respuesta -> Mono.error(
						new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cita " + id + " no existe")))
				.bodyToMono(CitaDto.class)
				.timeout(Duration.ofSeconds(3))
				.onErrorMap(error -> !(error instanceof ResponseStatusException), error -> new ResponseStatusException(
						HttpStatus.SERVICE_UNAVAILABLE, "El servicio de citas no esta disponible"));
	}

	public Mono<CitaDto> MarcarAtendida(Long id) {
		return webClientBuilder.build()
				.put()
				.uri(http + "/citas/{id}/estado", id)
				.bodyValue(new EstadoDto("ATENDIDA"))
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, respuesta -> Mono.error(
						new ResponseStatusException(HttpStatus.CONFLICT, "La cita " + id + " no se pudo marcar como ATENDIDA")))
				.bodyToMono(CitaDto.class)
				.timeout(Duration.ofSeconds(3))
				.onErrorMap(error -> !(error instanceof ResponseStatusException), error -> new ResponseStatusException(
						HttpStatus.SERVICE_UNAVAILABLE, "El servicio de citas no esta disponible"));
	}

}
