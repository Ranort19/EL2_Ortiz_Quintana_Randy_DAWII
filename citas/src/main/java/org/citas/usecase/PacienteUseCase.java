package org.citas.usecase;

import java.time.Duration;

import org.citas.bean.dto.PacienteDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class PacienteUseCase {

	String http = "http://pacientes";

	private final WebClient.Builder webClientBuilder;

	public Mono<PacienteDto> BuscarPaciente(Long id) {
		return webClientBuilder.build()
				.get()
				.uri(http + "/pacientes/{id}", id)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, respuesta -> Mono.error(
						new ResponseStatusException(HttpStatus.BAD_REQUEST, "El paciente " + id + " no existe")))
				.bodyToMono(PacienteDto.class)
				.timeout(Duration.ofSeconds(3))
				.onErrorMap(error -> !(error instanceof ResponseStatusException), error -> new ResponseStatusException(
						HttpStatus.SERVICE_UNAVAILABLE, "El servicio de pacientes no esta disponible"));
	}

}
