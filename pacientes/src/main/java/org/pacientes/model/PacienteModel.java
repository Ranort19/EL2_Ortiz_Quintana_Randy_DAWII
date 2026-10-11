package org.pacientes.model;

import java.time.LocalDate;
import java.util.List;

import org.pacientes.bean.entity.Paciente;
import org.pacientes.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PacienteModel {

	private static final List<String> TIPOS_DOCUMENTO = List.of("DNI", "CE", "PASAPORTE");

	private static final List<String> ESTADOS = List.of("ACTIVO", "INACTIVO");

	private final PacienteRepository pacienteRepository;

	public Mono<Paciente> RegistrarPaciente(Paciente paciente) {
		String error = Validar(paciente);
		if (error != null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, error));
		}

		paciente.setId(null);
		paciente.setTipoDocumento(paciente.getTipoDocumento().toUpperCase());
		paciente.setEstado(paciente.getEstado() == null ? "ACTIVO" : paciente.getEstado().toUpperCase());
		paciente.setFechaRegistro(LocalDate.now());

		return pacienteRepository.findByTipoDocumentoAndNumeroDocumento(paciente.getTipoDocumento(), paciente.getNumeroDocumento())
				.flatMap(existe -> Mono.<Paciente>error(new ResponseStatusException(HttpStatus.CONFLICT,
						"Ya existe un paciente con documento " + paciente.getTipoDocumento() + " " + paciente.getNumeroDocumento())))
				.switchIfEmpty(Mono.defer(() -> pacienteRepository.save(paciente)));
	}

	public Flux<Paciente> ListarPacientes() {
		return pacienteRepository.findAll();
	}

	public Mono<Paciente> BuscarPorId(Long id) {
		return pacienteRepository.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Paciente " + id + " no encontrado")));
	}

	public Mono<Paciente> BuscarPorDocumento(String tipoDocumento, String numeroDocumento) {
		return pacienteRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento.toUpperCase(), numeroDocumento)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Paciente con documento " + tipoDocumento + " " + numeroDocumento + " no encontrado")));
	}

	public Mono<Paciente> ActualizarPaciente(Long id, Paciente datos) {
		String error = Validar(datos);
		if (error != null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, error));
		}

		datos.setTipoDocumento(datos.getTipoDocumento().toUpperCase());

		return BuscarPorId(id)
				.flatMap(actual -> pacienteRepository.findByTipoDocumentoAndNumeroDocumento(datos.getTipoDocumento(), datos.getNumeroDocumento())
						.filter(otro -> !otro.getId().equals(id))
						.flatMap(otro -> Mono.<Paciente>error(new ResponseStatusException(HttpStatus.CONFLICT,
								"Ya existe otro paciente con documento " + datos.getTipoDocumento() + " " + datos.getNumeroDocumento())))
						.switchIfEmpty(Mono.defer(() -> {
							datos.setId(id);
							datos.setFechaRegistro(actual.getFechaRegistro());
							datos.setEstado(datos.getEstado() == null ? actual.getEstado() : datos.getEstado().toUpperCase());
							return pacienteRepository.save(datos);
						})));
	}

	private String Validar(Paciente paciente) {
		if (paciente.getTipoDocumento() == null || !TIPOS_DOCUMENTO.contains(paciente.getTipoDocumento().toUpperCase())) {
			return "Tipo de documento invalido. Use: " + TIPOS_DOCUMENTO;
		}
		if (Vacio(paciente.getNumeroDocumento())) {
			return "El numero de documento es obligatorio";
		}
		if (Vacio(paciente.getNombres()) || Vacio(paciente.getApellidos())) {
			return "Los nombres y apellidos son obligatorios";
		}
		if (paciente.getFechaNacimiento() == null || paciente.getFechaNacimiento().isAfter(LocalDate.now())) {
			return "La fecha de nacimiento es obligatoria y no puede ser futura";
		}
		if (Vacio(paciente.getTelefono()) || Vacio(paciente.getCorreo())) {
			return "El telefono y el correo de contacto son obligatorios";
		}
		if (paciente.getEstado() != null && !ESTADOS.contains(paciente.getEstado().toUpperCase())) {
			return "Estado invalido. Use: " + ESTADOS;
		}
		return null;
	}

	private boolean Vacio(String valor) {
		return valor == null || valor.isBlank();
	}

}
