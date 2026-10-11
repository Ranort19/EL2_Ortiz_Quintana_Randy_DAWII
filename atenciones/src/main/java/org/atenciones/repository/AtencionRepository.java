package org.atenciones.repository;

import org.atenciones.bean.entity.Atencion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface AtencionRepository extends ReactiveCrudRepository<Atencion, Long> {

	Mono<Atencion> findByCitaId(Long citaId);

	Mono<Boolean> existsByCitaId(Long citaId);

}
