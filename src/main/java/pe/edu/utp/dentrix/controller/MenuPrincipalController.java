package pe.edu.utp.dentrix.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import pe.edu.utp.dentrix.config.AppContext;
import pe.edu.utp.dentrix.config.SesionActual;
import pe.edu.utp.dentrix.ui.Navegador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class MenuPrincipalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuPrincipalController.class);

    private static final String FXML_LOGIN = "/pe/edu/utp/dentrix/fxml/login.fxml";
    private static final String FXML_SALA_ESPERA = "/pe/edu/utp/dentrix/fxml/sala-espera.fxml";
    private static final String FXML_PACIENTES = "/pe/edu/utp/dentrix/fxml/pacientes.fxml";
    private static final String FXML_AGENDA = "/pe/edu/utp/dentrix/fxml/agenda.fxml";
    private static final String FXML_FICHA = "/pe/edu/utp/dentrix/fxml/ficha-clinica.fxml";
    private static final String FXML_CAJA = "/pe/edu/utp/dentrix/fxml/caja.fxml";
    private static final String FXML_REPORTES = "/pe/edu/utp/dentrix/fxml/reportes.fxml";
    private static final String FXML_CONFIGURACION = "/pe/edu/utp/dentrix/fxml/configuracion.fxml";
    private static final String FXML_USUARIOS = "/pe/edu/utp/dentrix/fxml/usuarios.fxml";

    @FXML
    private Label etiquetaUsuario;

    @FXML
    private Label etiquetaRol;

    @FXML
    private Label etiquetaAvatar;

    @FXML
    private Label etiquetaPantalla;

    @FXML
    private StackPane panelContenido;

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnSalaEspera;

    @FXML
    private Button btnPacientes;

    @FXML
    private Button btnAgenda;

    @FXML
    private Button btnFicha;

    @FXML
    private Button btnCaja;

    @FXML
    private Button btnReportes;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Button btnConfiguracion;

    private List<Button> botonesNav;

    @FXML
    private void initialize() {
        SesionActual sesion = AppContext.get().sesion();
        if (!sesion.activa()) {
            return;
        }
        botonesNav = List.of(btnInicio, btnSalaEspera, btnPacientes, btnAgenda, btnFicha, btnCaja,
                btnReportes, btnUsuarios, btnConfiguracion);
        String nombre = sesion.getNombreCompleto() == null ? "" : sesion.getNombreCompleto().trim();
        etiquetaUsuario.setText(nombre);
        etiquetaAvatar.setText(nombre.isEmpty() ? "" : nombre.substring(0, 1).toUpperCase());
        etiquetaRol.setText(sesion.getRol().getNombre());
        String codigo = sesion.getCodigoRol();
        boolean esAdmin = "ADMINISTRADOR".equals(codigo);
        boolean esOdontologo = "ODONTOLOGO".equals(codigo);
        boolean esRecepcion = "RECEPCION".equals(codigo);
        mostrar(btnSalaEspera, esAdmin || esOdontologo || esRecepcion);
        mostrar(btnPacientes, esAdmin || esRecepcion);
        mostrar(btnAgenda, esAdmin || esOdontologo || esRecepcion);
        mostrar(btnFicha, esAdmin || esOdontologo);
        mostrar(btnCaja, esAdmin || esRecepcion);
        mostrar(btnReportes, esAdmin);
        mostrar(btnUsuarios, esAdmin);
        mostrar(btnConfiguracion, esAdmin);
        Navegador.registrar(this::navegarA);
        navegarA(Navegador.INICIO);
    }

    private void mostrar(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    private void activar(Button boton) {
        if (botonesNav != null) {
            for (Button b : botonesNav) {
                b.getStyleClass().remove("nav-activo");
            }
        }
        if (boton != null) {
            boton.getStyleClass().add("nav-activo");
        }
    }

    public void navegarA(String fxml) {
        if (fxml == null) {
            return;
        }
        Button botonCorrespondiente = null;
        String titulo = "";

        if (Navegador.INICIO.equals(fxml)) {
            botonCorrespondiente = btnInicio;
            titulo = "— Consola Principal";
        } else if (FXML_SALA_ESPERA.equals(fxml)) {
            botonCorrespondiente = btnSalaEspera;
            titulo = "— Sala de espera";
        } else if (FXML_PACIENTES.equals(fxml)) {
            botonCorrespondiente = btnPacientes;
            titulo = "— Pacientes";
        } else if (FXML_AGENDA.equals(fxml)) {
            botonCorrespondiente = btnAgenda;
            titulo = "— Agenda";
        } else if (FXML_FICHA.equals(fxml)) {
            botonCorrespondiente = btnFicha;
            titulo = "— Ficha clínica";
        } else if (FXML_CAJA.equals(fxml)) {
            botonCorrespondiente = btnCaja;
            titulo = "— Caja / Cobros";
        } else if (FXML_REPORTES.equals(fxml)) {
            botonCorrespondiente = btnReportes;
            titulo = "— Reportes";
        } else if (FXML_USUARIOS.equals(fxml)) {
            botonCorrespondiente = btnUsuarios;
            titulo = "— Usuarios y accesos";
        } else if (FXML_CONFIGURACION.equals(fxml)) {
            botonCorrespondiente = btnConfiguracion;
            titulo = "— Configuración";
        }

        if (botonCorrespondiente != null) {
            activar(botonCorrespondiente);
        }
        if (!titulo.isEmpty()) {
            etiquetaPantalla.setText(titulo);
        }
        cargar(fxml);
    }

    private void cargar(String fxml) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource(fxml));
            panelContenido.getChildren().setAll(raiz);
        } catch (Exception e) {
            LOGGER.error("Error al cargar pantalla FXML {}: {}", fxml, e.getMessage(), e);
            String mensaje = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                mensaje = e.getCause().getMessage();
            }
            mostrarAlerta("No se pudo abrir la pantalla: " + mensaje);
        }
    }

    @FXML
    private void onModuloClick(ActionEvent evento) {
        Button boton = (Button) evento.getSource();
        if (boton == btnInicio) {
            navegarA(Navegador.INICIO);
        } else if (boton == btnSalaEspera) {
            navegarA(FXML_SALA_ESPERA);
        } else if (boton == btnPacientes) {
            navegarA(FXML_PACIENTES);
        } else if (boton == btnAgenda) {
            navegarA(FXML_AGENDA);
        } else if (boton == btnFicha) {
            navegarA(FXML_FICHA);
        } else if (boton == btnCaja) {
            navegarA(FXML_CAJA);
        } else if (boton == btnReportes) {
            navegarA(FXML_REPORTES);
        } else if (boton == btnUsuarios) {
            navegarA(FXML_USUARIOS);
        } else if (boton == btnConfiguracion) {
            navegarA(FXML_CONFIGURACION);
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Dentrix");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void onSalirClick() throws IOException {
        AppContext.get().sesion().cerrar();
        Navegador.limpiar();
        Parent root = FXMLLoader.load(getClass().getResource(FXML_LOGIN));
        btnInicio.getScene().setRoot(root);
    }
}
