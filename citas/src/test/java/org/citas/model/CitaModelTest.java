package org.citas.model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.citas.bean.dto.CitaEventoDto;
import org.citas.bean.dto.EstadoDto;
import org.citas.bean.dto.PacienteDto;
import org.citas.bean.entity.Cita;
import org.citas.repository.CitaRepository;
import org.citas.usecase.PacienteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CitaModelTest {

	@Mock
	private CitaRepository citaRepository;

	@Mock
	private PacienteUseCase pacienteUseCase;

	@Mock
	private RabbitMQModel rabbitMQModel;

	@InjectMocks
	private CitaModel citaModel;

	private Cita CitaFicticia(Long id, String estado) {
		return new Cita(id, "CIT-1", 1L, "PEDIATRIA", LocalDateTime.now().plusDays(5).withSecond(0).withNano(0),
				"MED-001", "Control administrativo", estado, LocalDateTime.now(), LocalDateTime.now());
	}

	@Test
	void RegistrarCitaCorrecta() {
		when(pacienteUseCase.BuscarPaciente(1L))
				.thenReturn(Mono.just(new PacienteDto(1L, "DNI", "90000001", "Ana", "Prueba Ficticia", "ACTIVO")));
		when(citaRepository.existsByProfesionalCodigoAndFechaHoraAndEstadoIn(anyString(), any(LocalDateTime.class), anyList()))
				.thenReturn(Mono.just(false));
		when(citaRepository.existsByPacienteIdAndFechaHoraAndEstadoIn(anyLong(), any(LocalDateTime.class), anyList()))
				.thenReturn(Mono.just(false));
		when(citaRepository.save(any(Cita.class))).thenAnswer(invocacion -> {
			Cita cita = invocacion.getArgument(0);
			cita.setId(1L);
			return Mono.just(cita);
		});
		when(rabbitMQModel.Publicar(any(CitaEventoDto.class))).thenReturn(Mono.empty());

		StepVerifier.create(citaModel.RegistrarCita(CitaFicticia(null, null)))
				.expectNextMatches(cita -> cita.getId() == 1L
						&& "PENDIENTE".equals(cita.getEstado())
						&& cita.getCodigo().startsWith("CIT-"))
				.verifyComplete();

		verify(rabbitMQModel).Publicar(argThat(evento -> "CitaRegistrada".equals(evento.getEvento())));
	}

	@Test
	void ConsultarCitaPorId() {
		when(citaRepository.findById(1L)).thenReturn(Mono.just(CitaFicticia(1L, "PENDIENTE")));

		StepVerifier.create(citaModel.BuscarPorId(1L))
				.expectNextMatches(cita -> cita.getId() == 1L && "PEDIATRIA".equals(cita.getEspecialidad()))
				.verifyComplete();
	}

	@Test
	void RechazarTransicionNoPermitida() {
		when(citaRepository.findById(1L)).thenReturn(Mono.just(CitaFicticia(1L, "CANCELADA")));

		StepVerifier.create(citaModel.CambiarEstado(1L, new EstadoDto("CONFIRMADA")))
				.expectErrorMatches(error -> error instanceof ResponseStatusException
						&& ((ResponseStatusException) error).getStatusCode() == HttpStatus.CONFLICT)
				.verify();

		verify(citaRepository, never()).save(any(Cita.class));
		verify(rabbitMQModel, never()).Publicar(any(CitaEventoDto.class));
	}

	@Test
	void ConfirmarCitaPublicaEventoCitaConfirmada() {
		when(citaRepository.findById(1L)).thenReturn(Mono.just(CitaFicticia(1L, "PENDIENTE")));
		when(citaRepository.save(any(Cita.class))).thenAnswer(invocacion -> Mono.just(invocacion.getArgument(0)));
		when(rabbitMQModel.Publicar(any(CitaEventoDto.class))).thenReturn(Mono.empty());

		StepVerifier.create(citaModel.CambiarEstado(1L, new EstadoDto("CONFIRMADA")))
				.expectNextMatches(cita -> "CONFIRMADA".equals(cita.getEstado()))
				.verifyComplete();

		verify(rabbitMQModel).Publicar(argThat(evento -> "CitaConfirmada".equals(evento.getEvento())
				&& "PENDIENTE".equals(evento.getEstadoAnterior())
				&& "CONFIRMADA".equals(evento.getEstadoNuevo())));
	}

}
