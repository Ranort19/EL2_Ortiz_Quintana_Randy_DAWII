package org.atenciones.controller;

import org.atenciones.bean.dto.EstadoDto;
import org.atenciones.bean.entity.Atencion;
import org.atenciones.model.AtencionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/atenciones")
@RestController
public class AtencionController {

	private final AtencionModel atencionModel;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Atencion> Registrar(@RequestBody Atencion atencion) {
		return atencionModel.RegistrarAtencion(atencion);
	}

	@GetMapping
	public Flux<Atencion> Listar() {
		return atencionModel.ListarAtenciones();
	}

	@GetMapping("/{id}")
	public Mono<Atencion> BuscarPorId(@PathVariable Long id) {
		return atencionModel.BuscarPorId(id);
	}

	@GetMapping("/cita/{citaId}")
	public Mono<Atencion> BuscarPorCita(@PathVariable Long citaId) {
		return atencionModel.BuscarPorCita(citaId);
	}

	@PutMapping("/{id}/estado")
	public Mono<Atencion> CambiarEstado(@PathVariable Long id, @RequestBody EstadoDto estadoDto) {
		return atencionModel.CambiarEstado(id, estadoDto);
	}

}
