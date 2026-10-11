package org.citas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.citas.bean.entity.Cita;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CitaRepository extends ReactiveCrudRepository<Cita, Long> {

	Flux<Cita> findByPacienteId(Long pacienteId);

	Mono<Boolean> existsByProfesionalCodigoAndFechaHoraAndEstadoIn(String profesionalCodigo, LocalDateTime fechaHora,
			List<String> estados);

	Mono<Boolean> existsByPacienteIdAndFechaHoraAndEstadoIn(Long pacienteId, LocalDateTime fechaHora,
			List<String> estados);

}
