package org.notificaciones.controller;

import java.util.List;

import org.notificaciones.model.NotificacionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/notificaciones")
@RestController
public class NotificacionController {

	private final NotificacionModel notificacionModel;

	@GetMapping
	public Mono<List<String>> Listar() {
		return notificacionModel.ListarNotificaciones();
	}

}
