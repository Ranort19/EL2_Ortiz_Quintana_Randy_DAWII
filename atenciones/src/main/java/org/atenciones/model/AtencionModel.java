package org.atenciones.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.atenciones.bean.dto.EstadoDto;
import org.atenciones.bean.entity.Atencion;
import org.atenciones.repository.AtencionRepository;
import org.atenciones.usecase.CitaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AtencionModel {

	private static final List<String> ESTADOS = List.of("REGISTRADA", "FINALIZADA", "ANULADA");

	private static final Map<String, List<String>> TRANSICIONES = Map.of(
			"REGISTRADA", List.of("FINALIZADA", "ANULADA"));

	private final AtencionRepository atencionRepository;
	private final CitaUseCase citaUseCase;

	public Mono<Atencion> RegistrarAtencion(Atencion atencion) {
		if (atencion.getCitaId() == null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar la cita"));
		}
		if (atencion.getArea() == null || atencion.getArea().isBlank()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el area de atencion"));
		}

		return citaUseCase.BuscarCita(atencion.getCitaId())
				.flatMap(cita -> {
					if (!"CONFIRMADA".equals(cita.getEstado())) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
								"Solo se puede registrar la atencion de una cita CONFIRMADA. Estado actual: " + cita.getEstado()));
					}
					return atencionRepository.existsByCitaId(cita.getId())
							.flatMap(existe -> {
								if (existe) {
									return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
											"La cita " + cita.getId() + " ya tiene una atencion registrada"));
								}
								return citaUseCase.MarcarAtendida(cita.getId());
							});
				})
				.flatMap(cita -> {
					atencion.setId(null);
					atencion.setCodigo("ATE-" + System.currentTimeMillis());
					atencion.setPacienteId(cita.getPacienteId());
					atencion.setFechaAtencion(LocalDateTime.now());
					atencion.setArea(atencion.getArea().toUpperCase());
					atencion.setEstado("REGISTRADA");
					return atencionRepository.save(atencion);
				});
	}

	public Flux<Atencion> ListarAtenciones() {
		return atencionRepository.findAll();
	}

	public Mono<Atencion> BuscarPorId(Long id) {
		return atencionRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Atencion " + id + " no encontrada")));
	}

	public Mono<Atencion> BuscarPorCita(Long citaId) {
		return atencionRepository.findByCitaId(citaId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"La cita " + citaId + " no tiene atencion registrada")));
	}

	public Mono<Atencion> CambiarEstado(Long id, EstadoDto estadoDto) {
		String nuevoEstado = estadoDto.getEstado() == null ? "" : estadoDto.getEstado().toUpperCase();

		if (!ESTADOS.contains(nuevoEstado)) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Estado invalido. Use: " + ESTADOS));
		}

		return BuscarPorId(id)
				.flatMap(atencion -> {
					if (!TRANSICIONES.getOrDefault(atencion.getEstado(), List.of()).contains(nuevoEstado)) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
								"No se puede cambiar una atencion " + atencion.getEstado() + " a " + nuevoEstado));
					}
					atencion.setEstado(nuevoEstado);
					return atencionRepository.save(atencion);
				});
	}

}
