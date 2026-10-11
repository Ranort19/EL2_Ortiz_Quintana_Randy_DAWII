package org.pacientes.controller;

import org.pacientes.bean.entity.Paciente;
import org.pacientes.model.PacienteModel;
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
@RequestMapping("/pacientes")
@RestController
public class PacienteController {

	private final PacienteModel pacienteModel;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Paciente> Registrar(@RequestBody Paciente paciente) {
		return pacienteModel.RegistrarPaciente(paciente);
	}

	@GetMapping
	public Flux<Paciente> Listar() {
		return pacienteModel.ListarPacientes();
	}

	@GetMapping("/{id}")
	public Mono<Paciente> BuscarPorId(@PathVariable Long id) {
		return pacienteModel.BuscarPorId(id);
	}

	@GetMapping("/documento/{tipoDocumento}/{numeroDocumento}")
	public Mono<Paciente> BuscarPorDocumento(@PathVariable String tipoDocumento, @PathVariable String numeroDocumento) {
		return pacienteModel.BuscarPorDocumento(tipoDocumento, numeroDocumento);
	}

	@PutMapping("/{id}")
	public Mono<Paciente> Actualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
		return pacienteModel.ActualizarPaciente(id, paciente);
	}

}
