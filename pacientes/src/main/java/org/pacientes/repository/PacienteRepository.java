package org.pacientes.repository;

import org.pacientes.bean.entity.Paciente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface PacienteRepository extends ReactiveCrudRepository<Paciente, Long> {

	Mono<Paciente> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);

}
