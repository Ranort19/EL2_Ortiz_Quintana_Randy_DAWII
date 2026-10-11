package org.notificaciones.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.notificaciones.bean.dto.CitaEventoDto;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NotificacionModel {

	private static final Map<String, String> TEXTOS = Map.of(
			"CitaRegistrada", "ha sido registrada",
			"CitaConfirmada", "ha sido confirmada",
			"CitaReprogramada", "ha sido reprogramada",
			"CitaCancelada", "ha sido cancelada",
			"CitaAtendida", "ha sido atendida",
			"CitaAusente", "ha sido marcada como ausente");

	private final List<String> notificaciones = new CopyOnWriteArrayList<>();

	public String Guardar(CitaEventoDto evento) {
		String mensaje = "[" + evento.getEvento() + "] Paciente " + evento.getPacienteId()
				+ " - Cita " + evento.getCodigo() + " del " + evento.getFechaHora() + ": Su cita "
				+ TEXTOS.getOrDefault(evento.getEvento(), "cambio a " + evento.getEstadoNuevo()) + ".";
		notificaciones.add(LocalDateTime.now().withNano(0) + " " + mensaje);
		return mensaje;
	}

	public Mono<List<String>> ListarNotificaciones() {
		return Flux.fromIterable(notificaciones).collectList();
	}

}
