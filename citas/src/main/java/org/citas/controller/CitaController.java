package org.citas.controller;

import org.citas.bean.dto.EstadoDto;
import org.citas.bean.dto.ReprogramacionDto;
import org.citas.bean.entity.Cita;
import org.citas.model.CitaModel;
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
@RequestMapping("/citas")
@RestController
public class CitaController {

	private final CitaModel citaModel;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Cita> Registrar(@RequestBody Cita cita) {
		return citaModel.RegistrarCita(cita);
	}

	@GetMapping
	public Flux<Cita> Listar() {
		return citaModel.ListarCitas();
	}

	@GetMapping("/{id}")
	public Mono<Cita> BuscarPorId(@PathVariable Long id) {
		return citaModel.BuscarPorId(id);
	}

	@GetMapping("/paciente/{pacienteId}")
	public Flux<Cita> ListarPorPaciente(@PathVariable Long pacienteId) {
		return citaModel.ListarPorPaciente(pacienteId);
	}

	@PutMapping("/{id}/estado")
	public Mono<Cita> CambiarEstado(@PathVariable Long id, @RequestBody EstadoDto estadoDto) {
		return citaModel.CambiarEstado(id, estadoDto);
	}

	@PutMapping("/{id}/reprogramar")
	public Mono<Cita> Reprogramar(@PathVariable Long id, @RequestBody ReprogramacionDto reprogramacionDto) {
		return citaModel.Reprogramar(id, reprogramacionDto);
	}

}
