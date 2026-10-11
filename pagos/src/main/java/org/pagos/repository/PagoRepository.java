package org.pagos.repository;

import org.pagos.bean.entity.Pago;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PagoRepository extends ReactiveCrudRepository<Pago, Long> {

	Flux<Pago> findByPacienteId(Long pacienteId);

	Mono<Boolean> existsByNumeroOperacion(String numeroOperacion);

}
