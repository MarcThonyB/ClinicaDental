package pe.edu.utp.dentrix.scratch;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pe.edu.utp.dentrix.config.AppContext;
import pe.edu.utp.dentrix.model.Pago;
import pe.edu.utp.dentrix.model.PlanTratamiento;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestLoadFxml {

    @AfterEach
    void tearDown() {
        AppContext.get().sesion().cerrar();
    }

    @Test
    public void testFullCajaLogic() throws Exception {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }

        var usuarioOpt = AppContext.get().usuarios().buscarPorUsername("jvallejos");
        if (usuarioOpt.isPresent()) {
            var usuario = usuarioOpt.get();
            var rol = AppContext.get().gestionUsuarios().listarRoles().stream()
                    .filter(r -> r.getId().equals(usuario.getRolId()))
                    .findFirst()
                    .orElse(null);
            if (rol != null) {
                AppContext.get().sesion().abrir(usuario, rol);
            }
        }

        AppContext ctx = AppContext.get();
        List<pe.edu.utp.dentrix.model.FichaClinica> fichas = ctx.fichas().listarCobrables();
        assertNotNull(fichas);

        List<Pago> saldos = ctx.caja().documentosConSaldo();
        assertNotNull(saldos);

        List<Pago> boletas = ctx.caja().boletasDelDia(LocalDate.now());
        assertNotNull(boletas);

        for (var f : fichas) {
            List<PlanTratamiento> plan = ctx.fichas().planPorFicha(f.getId());
            assertNotNull(plan);
        }

        Parent caja = FXMLLoader.load(getClass().getResource("/pe/edu/utp/dentrix/fxml/caja.fxml"));
        assertNotNull(caja, "caja.fxml debe cargar correctamente");

        String[] vistas = {
                "/pe/edu/utp/dentrix/fxml/menu-principal.fxml",
                "/pe/edu/utp/dentrix/fxml/dashboard.fxml",
                "/pe/edu/utp/dentrix/fxml/ficha-clinica.fxml",
                "/pe/edu/utp/dentrix/fxml/agenda.fxml",
                "/pe/edu/utp/dentrix/fxml/sala-espera.fxml",
                "/pe/edu/utp/dentrix/fxml/pacientes.fxml",
                "/pe/edu/utp/dentrix/fxml/reportes.fxml",
                "/pe/edu/utp/dentrix/fxml/usuarios.fxml",
                "/pe/edu/utp/dentrix/fxml/configuracion.fxml",
                "/pe/edu/utp/dentrix/fxml/login.fxml"
        };

        for (String vista : vistas) {
            Parent p = FXMLLoader.load(getClass().getResource(vista));
            assertNotNull(p, "Vista debe cargar: " + vista);
        }

        // Breve espera para tareas en segundo plano
        Thread.sleep(500);
    }
}
