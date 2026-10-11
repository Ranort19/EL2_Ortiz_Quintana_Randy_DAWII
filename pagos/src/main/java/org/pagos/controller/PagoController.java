package org.pagos.controller;

import org.pagos.bean.entity.Pago;
import org.pagos.model.PagoModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/pagos")
@RestController
public class PagoController {

	private final PagoModel pagoModel;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Pago> RegistrarPago(@RequestBody Pago pago) {
		return pagoModel.RegistrarPago(pago);
	}

	@GetMapping
	public Flux<Pago> Listar() {
		return pagoModel.ListarPagos();
	}

	@GetMapping("/paciente/{pacienteId}")
	public Flux<Pago> ListarPorPaciente(@PathVariable Long pacienteId) {
		return pagoModel.ListarPorPaciente(pacienteId);
	}

}
