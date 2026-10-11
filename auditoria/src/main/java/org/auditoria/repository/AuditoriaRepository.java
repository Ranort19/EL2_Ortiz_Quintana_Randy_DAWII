package org.auditoria.repository;

import org.auditoria.bean.entity.Auditoria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface AuditoriaRepository extends ReactiveCrudRepository<Auditoria, Long> {

	Flux<Auditoria> findByRecursoIdOrderByFechaAsc(Long recursoId);

}
