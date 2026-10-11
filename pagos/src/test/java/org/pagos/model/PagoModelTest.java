package org.pagos.model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pagos.bean.dto.PacienteDto;
import org.pagos.bean.entity.Pago;
import org.pagos.repository.PagoRepository;
import org.pagos.usecase.PacienteUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PagoModelTest {

	@Mock
	private PagoRepository pagoRepository;

	@Mock
	private PacienteUseCase pacienteUseCase;

	@InjectMocks
	private PagoModel pagoModel;

	private Pago PagoFicticio(Double monto) {
		return new Pago(null, 1L, "Consulta externa", monto, null, "EFECTIVO", "OP-0001", null);
	}

	private boolean EsError(Throwable error, HttpStatus estado) {
		return error instanceof ResponseStatusException
				&& ((ResponseStatusException) error).getStatusCode() == estado;
	}

	@Test
	void RechazarMontoCero() {
		StepVerifier.create(pagoModel.RegistrarPago(PagoFicticio(0.0)))
				.expectErrorMatches(error -> EsError(error, HttpStatus.BAD_REQUEST))
				.verify();

		verifyNoInteractions(pagoRepository, pacienteUseCase);
	}

	@Test
	void RechazarMontoNegativo() {
		StepVerifier.create(pagoModel.RegistrarPago(PagoFicticio(-50.0)))
				.expectErrorMatches(error -> EsError(error, HttpStatus.BAD_REQUEST))
				.verify();

		verifyNoInteractions(pagoRepository, pacienteUseCase);
	}

	@Test
	void RechazarNumeroOperacionDuplicado() {
		when(pagoRepository.existsByNumeroOperacion("OP-0001")).thenReturn(Mono.just(true));

		StepVerifier.create(pagoModel.RegistrarPago(PagoFicticio(50.0)))
				.expectErrorMatches(error -> EsError(error, HttpStatus.CONFLICT))
				.verify();

		verify(pagoRepository, never()).save(any(Pago.class));
	}

	@Test
	void RegistrarPagoValido() {
		when(pagoRepository.existsByNumeroOperacion("OP-0001")).thenReturn(Mono.just(false));
		when(pacienteUseCase.BuscarPaciente(1L))
				.thenReturn(Mono.just(new PacienteDto(1L, "DNI", "90000001", "Ana", "Prueba Ficticia", "ACTIVO")));
		when(pagoRepository.save(any(Pago.class))).thenAnswer(invocacion -> Mono.just(invocacion.getArgument(0)));

		StepVerifier.create(pagoModel.RegistrarPago(PagoFicticio(50.0)))
				.expectNextMatches(pago -> "REGISTRADO".equals(pago.getEstado()) && pago.getFechaPago() != null)
				.verifyComplete();
	}

}
