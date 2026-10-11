package org.auditoria.controller;

import org.auditoria.bean.entity.Auditoria;
import org.auditoria.model.AuditoriaModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RequestMapping("/auditoria")
@RestController
public class AuditoriaController {

	private final AuditoriaModel auditoriaModel;

	@GetMapping
	public Flux<Auditoria> Listar() {
		return auditoriaModel.ListarRegistros();
	}

	@GetMapping("/recurso/{recursoId}")
	public Flux<Auditoria> ListarPorRecurso(@PathVariable Long recursoId) {
		return auditoriaModel.ListarPorRecurso(recursoId);
	}

}
