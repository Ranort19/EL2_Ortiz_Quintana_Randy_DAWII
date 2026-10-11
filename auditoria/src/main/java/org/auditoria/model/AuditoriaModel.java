package org.auditoria.model;

import java.time.LocalDateTime;

import org.auditoria.bean.dto.CitaEventoDto;
import org.auditoria.bean.entity.Auditoria;
import org.auditoria.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuditoriaModel {

	private final AuditoriaRepository auditoriaRepository;

	public Mono<Auditoria> Registrar(CitaEventoDto evento) {
		Auditoria auditoria = new Auditoria(null,
				evento.getFecha() != null ? evento.getFecha() : LocalDateTime.now(),
				"CITA", evento.getCitaId(), evento.getEvento(),
				evento.getEstadoAnterior(), evento.getEstadoNuevo(), evento.getOrigen());
		return auditoriaRepository.save(auditoria);
	}

	public Flux<Auditoria> ListarRegistros() {
		return auditoriaRepository.findAll();
	}

	public Flux<Auditoria> ListarPorRecurso(Long recursoId) {
		return auditoriaRepository.findByRecursoIdOrderByFechaAsc(recursoId);
	}

}
