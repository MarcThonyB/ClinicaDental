package pe.edu.utp.dentrix.service;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.utp.dentrix.dao.RolDAO;
import pe.edu.utp.dentrix.dao.UsuarioDAO;
import pe.edu.utp.dentrix.excepcion.DentrixException;
import pe.edu.utp.dentrix.model.Rol;
import pe.edu.utp.dentrix.model.Usuario;
import pe.edu.utp.dentrix.util.PasswordUtil;

import java.time.LocalDateTime;

public class AutenticacionService {

    private static final Logger log = LoggerFactory.getLogger(AutenticacionService.class);

    private static final String ESTADO_BLOQUEADO = "BLOQUEADO";
    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final int MAXIMO_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 15;

    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;

    public AutenticacionService(UsuarioDAO usuarioDAO, RolDAO rolDAO) {
        this.usuarioDAO = Preconditions.checkNotNull(usuarioDAO, "usuarioDAO no puede ser nulo");
        this.rolDAO = Preconditions.checkNotNull(rolDAO, "rolDAO no puede ser nulo");
    }

    public Usuario iniciarSesion(String username, String clave) throws DentrixException {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(clave)) {
            throw new DentrixException("Usuario o contraseña incorrectos");
        }

        String usernameLimpio = StringUtils.trim(username);
        log.info("Intento de inicio de sesión para el usuario: '{}'", usernameLimpio);

        Usuario usuario = usuarioDAO.buscarPorUsername(usernameLimpio)
                .orElseThrow(() -> {
                    log.warn("Intento de inicio de sesión con usuario inexistente: '{}'", usernameLimpio);
                    return new DentrixException("Usuario o contraseña incorrectos");
                });

        if ("INACTIVO".equals(usuario.getEstado())) {
            log.warn("Intento de acceso denegado: el usuario '{}' se encuentra INACTIVO", usernameLimpio);
            throw new DentrixException("El usuario está inactivo. Pide al administrador que lo reactive.");
        }

        if (ESTADO_BLOQUEADO.equals(usuario.getEstado())) {
            LocalDateTime ahora = LocalDateTime.now();
            if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isBefore(ahora)) {
                usuario.setEstado(ESTADO_ACTIVO);
                usuario.setIntentosFallidos(0);
                usuario.setBloqueadoHasta(null);
                usuarioDAO.actualizar(usuario);
                log.info("Bloqueo temporal expirado para el usuario '{}', cuenta restablecida a ACTIVO", usernameLimpio);
            } else {
                log.warn("Acceso denegado: usuario '{}' bloqueado temporalmente hasta {}", usernameLimpio, usuario.getBloqueadoHasta());
                throw new DentrixException("Usuario bloqueado hasta " + usuario.getBloqueadoHasta());
            }
        }

        if (!PasswordUtil.verificar(clave, usuario.getPasswordHash())) {
            int intentos = (usuario.getIntentosFallidos() == null ? 0 : usuario.getIntentosFallidos()) + 1;
            usuario.setIntentosFallidos(intentos);
            if (intentos >= MAXIMO_INTENTOS) {
                usuario.setEstado(ESTADO_BLOQUEADO);
                usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
                log.warn("Usuario '{}' bloqueado por 15 minutos al superar el máximo de {} intentos fallidos", usernameLimpio, MAXIMO_INTENTOS);
            } else {
                log.warn("Contraseña incorrecta para el usuario '{}'. Intento {} de {}", usernameLimpio, intentos, MAXIMO_INTENTOS);
            }
            usuarioDAO.actualizar(usuario);
            throw new DentrixException("Usuario o contraseña incorrectos");
        }

        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioDAO.actualizar(usuario);
        log.info("Sesión iniciada exitosamente para el usuario '{}' (Rol ID: {})", usernameLimpio, usuario.getRolId());
        return usuario;
    }

    public Rol rolDe(Usuario usuario) throws DentrixException {
        return rolDAO.buscarPorId(usuario.getRolId()).orElse(null);
    }

    public boolean esOdontologo(Usuario usuario) throws DentrixException {
        Rol rol = rolDe(usuario);
        return rol != null && "ODONTOLOGO".equals(rol.getCodigo()) && usuario.getColegiatura() != null;
    }
}
