package org.pagos.model;

import java.time.LocalDate;

import org.pagos.bean.entity.Pago;
import org.pagos.repository.PagoRepository;
import org.pagos.usecase.PacienteUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PagoModel {

	private final PagoRepository pagoRepository;
	private final PacienteUseCase pacienteUseCase;

	public Mono<Pago> RegistrarPago(Pago pago) {
		if (pago.getPacienteId() == null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el paciente"));
		}
		if (pago.getMonto() == null || pago.getMonto() <= 0) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El monto del pago debe ser mayor a 0"));
		}
		if (pago.getConcepto() == null || pago.getConcepto().isBlank()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el concepto del pago"));
		}
		if (pago.getMedioPago() == null || pago.getMedioPago().isBlank()
				|| pago.getNumeroOperacion() == null || pago.getNumeroOperacion().isBlank()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Debe indicar el medio de pago y el numero de operacion"));
		}

		return pagoRepository.existsByNumeroOperacion(pago.getNumeroOperacion())
				.flatMap(existe -> {
					if (existe) {
						return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
								"El numero de operacion " + pago.getNumeroOperacion() + " ya fue registrado"));
					}
					return pacienteUseCase.BuscarPaciente(pago.getPacienteId());
				})
				.flatMap(paciente -> {
					pago.setId(null);
					pago.setMedioPago(pago.getMedioPago().toUpperCase());
					pago.setFechaPago(LocalDate.now());
					pago.setEstado("REGISTRADO");
					return pagoRepository.save(pago);
				});
	}

	public Flux<Pago> ListarPagos() {
		return pagoRepository.findAll();
	}

	public Flux<Pago> ListarPorPaciente(Long pacienteId) {
		return pagoRepository.findByPacienteId(pacienteId);
	}

}
