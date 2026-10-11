package org.citas.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.citas.bean.dto.CitaEventoDto;
import org.citas.bean.dto.EstadoDto;
import org.citas.bean.dto.ReprogramacionDto;
import org.citas.bean.entity.Cita;
import org.citas.repository.CitaRepository;
import org.citas.usecase.PacienteUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CitaModel {

	private static final List<String> ESPECIALIDADES = List.of("MEDICINA_GENERAL", "PEDIATRIA", "CARDIOLOGIA",
			"GINECOLOGIA", "TRAUMATOLOGIA", "DERMATOLOGIA");

	private static final List<String> ESTADOS = List.of("PENDIENTE", "CONFIRMADA", "ATENDIDA", "CANCELADA", "AUSENTE");

	private static final List<String> ACTIVAS = List.of("PENDIENTE", "CONFIRMADA");

	private static final Map<String, List<String>> TRANSICIONES = Map.of(
			"PENDIENTE", List.of("CONFIRMADA", "CANCELADA"),
			"CONFIRMADA", List.of("ATENDIDA", "CANCELADA", "AUSENTE"));

	private static final Map<String, String> EVENTOS = Map.of(
			"CONFIRMADA", "CitaConfirmada",
			"ATENDIDA", "CitaAtendida",
			"CANCELADA", "CitaCancelada",
			"AUSENTE", "CitaAusente");

	private final CitaRepository citaRepository;
	private final PacienteUseCase pacienteUseCase;
	private final RabbitMQModel rabbitMQModel;

	public Mono<Cita> RegistrarCita(Cita cita) {
		if (cita.getPacienteId() == null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el paciente"));
		}
		if (cita.getEspecialidad() == null || !ESPECIALIDADES.contains(cita.getEspecialidad().toUpperCase())) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Especialidad invalida. Use: " + ESPECIALIDADES));
		}
		if (cita.getFechaHora() == null || !cita.getFechaHora().isAfter(LocalDateTime.now())) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"La fecha y hora de la cita es obligatoria y debe ser futura"));
		}
		if (cita.getProfesionalCodigo() == null || cita.getProfesionalCodigo().isBlank()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Debe indicar el codigo del profesional"));
		}
		if (cita.getMotivo() == null || cita.getMotivo().isBlank() || cita.getMotivo().length() > 150) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El motivo administrativo es obligatorio y debe tener como maximo 150 caracteres"));
		}

		cita.setEspecialidad(cita.getEspecialidad().toUpperCase());
		cita.setFechaHora(cita.getFechaHora().withSecond(0).withNano(0));

		return pacienteUseCase.BuscarPaciente(cita.getPacienteId())
				.flatMap(paciente -> {
					if (!"ACTIVO".equals(paciente.getEstado())) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"El paciente no se encuentra ACTIVO"));
					}
					return ValidarAgenda(cita.getProfesionalCodigo(), cita.getPacienteId(), cita.getFechaHora());
				})
				.then(Mono.defer(() -> {
					cita.setId(null);
					cita.setCodigo("CIT-" + System.currentTimeMillis());
					cita.setEstado("PENDIENTE");
					cita.setFechaCreacion(LocalDateTime.now());
					cita.setFechaActualizacion(LocalDateTime.now());
					return citaRepository.save(cita);
				}))
				.flatMap(c -> PublicarEvento(c, "CitaRegistrada", null));
	}

	public Flux<Cita> ListarCitas() {
		return citaRepository.findAll();
	}

	public Mono<Cita> BuscarPorId(Long id) {
		return citaRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Cita " + id + " no encontrada")));
	}

	public Flux<Cita> ListarPorPaciente(Long pacienteId) {
		return citaRepository.findByPacienteId(pacienteId);
	}

	public Mono<Cita> CambiarEstado(Long id, EstadoDto estadoDto) {
		String nuevoEstado = estadoDto.getEstado() == null ? "" : estadoDto.getEstado().toUpperCase();

		if (!ESTADOS.contains(nuevoEstado)) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Estado invalido. Use: " + ESTADOS));
		}

		return BuscarPorId(id)
				.flatMap(cita -> {
					String estadoAnterior = cita.getEstado();
					if (!TRANSICIONES.getOrDefault(estadoAnterior, List.of()).contains(nuevoEstado)) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
								"No se puede cambiar una cita " + estadoAnterior + " a " + nuevoEstado));
					}
					cita.setEstado(nuevoEstado);
					cita.setFechaActualizacion(LocalDateTime.now());
					return citaRepository.save(cita)
							.flatMap(c -> PublicarEvento(c, EVENTOS.get(nuevoEstado), estadoAnterior));
				});
	}

	public Mono<Cita> Reprogramar(Long id, ReprogramacionDto reprogramacionDto) {
		if (reprogramacionDto.getFechaHora() == null || !reprogramacionDto.getFechaHora().isAfter(LocalDateTime.now())) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"La nueva fecha y hora es obligatoria y debe ser futura"));
		}

		LocalDateTime nuevaFecha = reprogramacionDto.getFechaHora().withSecond(0).withNano(0);

		return BuscarPorId(id)
				.flatMap(cita -> {
					if (!ACTIVAS.contains(cita.getEstado())) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
								"Solo se puede reprogramar una cita PENDIENTE o CONFIRMADA. Estado actual: " + cita.getEstado()));
					}
					if (nuevaFecha.equals(cita.getFechaHora())) {
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"La nueva fecha y hora debe ser distinta a la actual"));
					}
					return ValidarAgenda(cita.getProfesionalCodigo(), cita.getPacienteId(), nuevaFecha)
							.then(Mono.defer(() -> {
								cita.setFechaHora(nuevaFecha);
								cita.setFechaActualizacion(LocalDateTime.now());
								return citaRepository.save(cita);
							}))
							.flatMap(c -> PublicarEvento(c, "CitaReprogramada", c.getEstado()));
				});
	}

	private Mono<Void> ValidarAgenda(String profesionalCodigo, Long pacienteId, LocalDateTime fechaHora) {
		return Mono.zip(
				citaRepository.existsByProfesionalCodigoAndFechaHoraAndEstadoIn(profesionalCodigo, fechaHora, ACTIVAS),
				citaRepository.existsByPacienteIdAndFechaHoraAndEstadoIn(pacienteId, fechaHora, ACTIVAS))
				.flatMap(t -> {
					if (t.getT1()) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
								"El profesional " + profesionalCodigo + " ya tiene una cita el " + fechaHora));
					}
					if (t.getT2()) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
								"El paciente " + pacienteId + " ya tiene una cita el " + fechaHora));
					}
					return Mono.empty();
				});
	}

	private Mono<Cita> PublicarEvento(Cita cita, String evento, String estadoAnterior) {
		CitaEventoDto citaEvento = new CitaEventoDto(evento, cita.getId(), cita.getCodigo(), cita.getPacienteId(),
				cita.getFechaHora(), estadoAnterior, cita.getEstado(), LocalDateTime.now(), "citas");
		return rabbitMQModel.Publicar(citaEvento).thenReturn(cita);
	}

}
