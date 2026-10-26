package pe.edu.utp.dentrix.service;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.utp.dentrix.dao.PacienteDAO;
import pe.edu.utp.dentrix.excepcion.DentrixException;
import pe.edu.utp.dentrix.model.Paciente;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    private final PacienteDAO pacienteDAO;

    public PacienteService(PacienteDAO pacienteDAO) {
        this.pacienteDAO = Preconditions.checkNotNull(pacienteDAO, "pacienteDAO no puede ser nulo");
    }

    public Paciente registrar(Paciente paciente) throws DentrixException {
        validarDni(paciente);
        if (pacienteDAO.buscarPorDni(paciente.getDni()).isPresent()) {
            throw new DentrixException("El DNI " + paciente.getDni() + " ya está registrado (RF-04)");
        }
        int maximo = pacienteDAO.listarTodos().stream()
                .map(Paciente::getId)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        paciente.setCodigoHc(String.format(Locale.ROOT, "HC-%06d", maximo + 1));
        paciente.setFechaApertura(LocalDate.now());
        paciente.setEstado("ACTIVO");
        try {
            paciente.setId(pacienteDAO.insertar(paciente));
            log.info("Paciente registrado exitosamente con HC: {} (DNI: {})", paciente.getCodigoHc(), paciente.getDni());
        } catch (DentrixException e) {
            log.warn("Fallo al registrar paciente DNI: {}: {}", paciente.getDni(), e.getMessage());
            throw esDuplicado(e)
                    ? new DentrixException("El DNI " + paciente.getDni() + " ya está registrado (RF-04)", e)
                    : traducir(e);
        }
        return paciente;
    }

    public Paciente editar(Paciente paciente) throws DentrixException {
        validarDni(paciente);
        if (paciente.getId() == null) {
            throw new DentrixException("El id del paciente es obligatorio para editar");
        }
        Paciente actual = pacienteDAO.buscarPorId(paciente.getId())
                .orElseThrow(() -> new DentrixException("Paciente no encontrado"));
        if (!Objects.equals(actual.getDni(), paciente.getDni())
                && pacienteDAO.buscarPorDni(paciente.getDni()).isPresent()) {
            throw new DentrixException("El DNI " + paciente.getDni() + " ya está registrado (RF-04)");
        }
        if (paciente.getFechaApertura() == null) {
            paciente.setFechaApertura(actual.getFechaApertura());
        }
        if (paciente.getEstado() == null) {
            paciente.setEstado(actual.getEstado());
        }
        if (paciente.getUsuarioRegistroId() == null) {
            paciente.setUsuarioRegistroId(actual.getUsuarioRegistroId());
        }
        try {
            if (!pacienteDAO.actualizar(paciente)) {
                throw new DentrixException("Paciente no encontrado");
            }
            log.info("Paciente ID: {} actualizado exitosamente", paciente.getId());
        } catch (DentrixException e) {
            log.warn("Fallo al actualizar paciente ID: {}: {}", paciente.getId(), e.getMessage());
            throw esDuplicado(e)
                    ? new DentrixException("El DNI " + paciente.getDni() + " ya está registrado (RF-04)", e)
                    : traducir(e);
        }
        return paciente;
    }

    public List<Paciente> listar() throws DentrixException {
        return pacienteDAO.listarTodos();
    }

    public List<Paciente> buscar(String texto) throws DentrixException {
        if (StringUtils.isBlank(texto)) {
            return pacienteDAO.listarTodos();
        }
        return pacienteDAO.listarPorTexto(StringUtils.trim(texto));
    }

    public Paciente buscarPorId(Integer id) throws DentrixException {
        return pacienteDAO.buscarPorId(id)
                .orElseThrow(() -> new DentrixException("Paciente no encontrado"));
    }

    public Optional<Paciente> buscarPorDni(String dni) throws DentrixException {
        return pacienteDAO.buscarPorDni(StringUtils.trimToNull(dni));
    }

    public void inactivar(Integer id) throws DentrixException {
        try {
            Paciente paciente = buscarPorId(id);
            paciente.setEstado("INACTIVO");
            if (!pacienteDAO.actualizar(paciente)) {
                throw new DentrixException("Paciente no encontrado");
            }
            log.info("Paciente ID: {} inactivado", id);
        } catch (DentrixException e) {
            throw traducir(e);
        }
    }

    private void validarDni(Paciente paciente) throws DentrixException {
        if (paciente == null) {
            throw new DentrixException("Los datos del paciente son obligatorios");
        }
        String dni = StringUtils.trimToNull(paciente.getDni());
        if (StringUtils.isEmpty(dni)) {
            throw new DentrixException("El DNI es obligatorio (RF-04)");
        }
        paciente.setDni(dni);
        if (!dni.matches("\\d{8}")) {
            throw new DentrixException("El DNI debe tener 8 dígitos: " + dni);
        }
    }

    private boolean esDuplicado(DentrixException e) {
        return e.getCause() instanceof SQLException sqle && "23505".equals(sqle.getSQLState());
    }

    private DentrixException traducir(DentrixException e) {
        if (e.getCause() instanceof SQLException sqle) {
            String estado = sqle.getSQLState();
            String mensaje = sqle.getMessage() == null ? "" : sqle.getMessage();
            if ("23505".equals(estado)) {
                return new DentrixException("Ya existe un paciente con los mismos datos (duplicado).", e);
            }
            if ("23514".equals(estado)) {
                return new DentrixException("Los datos del paciente no cumplen una regla de validación.", e);
            }
            if ("23502".equals(estado)) {
                return new DentrixException("Faltan datos obligatorios del paciente.", e);
            }
            if (mensaje.contains("RF-")) {
                return new DentrixException("Regla de negocio: " + mensaje, e);
            }
        }
        return e;
    }
}
