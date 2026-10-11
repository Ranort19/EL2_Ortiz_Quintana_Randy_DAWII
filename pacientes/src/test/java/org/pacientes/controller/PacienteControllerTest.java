package org.pacientes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.pacientes.bean.entity.Paciente;
import org.pacientes.model.PacienteModel;
import org.pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@WebFluxTest(PacienteController.class)
@Import(PacienteModel.class)
class PacienteControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private PacienteRepository pacienteRepository;

	private Paciente PacienteFicticio(Long id) {
		return new Paciente(id, "DNI", "90000001", "Ana", "Prueba Ficticia", LocalDate.of(1990, 5, 10),
				"900000001", "ana@correo.test", null, LocalDate.now(), "ACTIVO");
	}

	@Test
	void RegistrarPacienteCorrecto() {
		when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento("DNI", "90000001")).thenReturn(Mono.empty());
		when(pacienteRepository.save(any(Paciente.class))).thenReturn(Mono.just(PacienteFicticio(1L)));

		webTestClient.post()
				.uri("/pacientes")
				.bodyValue(PacienteFicticio(null))
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.jsonPath("$.id").isEqualTo(1)
				.jsonPath("$.numeroDocumento").isEqualTo("90000001")
				.jsonPath("$.estado").isEqualTo("ACTIVO");
	}

	@Test
	void RechazarDocumentoDuplicado() {
		when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento("DNI", "90000001"))
				.thenReturn(Mono.just(PacienteFicticio(1L)));

		webTestClient.post()
				.uri("/pacientes")
				.bodyValue(PacienteFicticio(null))
				.exchange()
				.expectStatus().isEqualTo(409);

		verify(pacienteRepository, never()).save(any(Paciente.class));
	}

	@Test
	void RechazarDatosInvalidos() {
		Paciente paciente = PacienteFicticio(null);
		paciente.setTipoDocumento("XYZ");

		webTestClient.post()
				.uri("/pacientes")
				.bodyValue(paciente)
				.exchange()
				.expectStatus().isBadRequest();

		verify(pacienteRepository, never()).save(any(Paciente.class));
	}

	@Test
	void ConsultarPacientePorId() {
		when(pacienteRepository.findById(1L)).thenReturn(Mono.just(PacienteFicticio(1L)));

		webTestClient.get()
				.uri("/pacientes/{id}", 1L)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(1)
				.jsonPath("$.nombres").isEqualTo("Ana");
	}

	@Test
	void ConsultarPacienteInexistente() {
		when(pacienteRepository.findById(99L)).thenReturn(Mono.empty());

		webTestClient.get()
				.uri("/pacientes/{id}", 99L)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void ActualizarPaciente() {
		Paciente actualizado = PacienteFicticio(1L);
		actualizado.setTelefono("900000002");

		when(pacienteRepository.findById(1L)).thenReturn(Mono.just(PacienteFicticio(1L)));
		when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento("DNI", "90000001"))
				.thenReturn(Mono.just(PacienteFicticio(1L)));
		when(pacienteRepository.save(any(Paciente.class))).thenReturn(Mono.just(actualizado));

		webTestClient.put()
				.uri("/pacientes/{id}", 1L)
				.bodyValue(actualizado)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.telefono").isEqualTo("900000002");
	}

}
