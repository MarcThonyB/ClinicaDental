package pe.edu.utp.dentrix.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.utp.dentrix.config.AppContext;
import pe.edu.utp.dentrix.dto.KpiDia;
import pe.edu.utp.dentrix.excepcion.DentrixException;
import pe.edu.utp.dentrix.model.Cita;
import pe.edu.utp.dentrix.model.Paciente;
import pe.edu.utp.dentrix.model.Tratamiento;
import pe.edu.utp.dentrix.model.Usuario;
import pe.edu.utp.dentrix.ui.Badges;
import pe.edu.utp.dentrix.ui.Navegador;
import pe.edu.utp.dentrix.ui.TareasFX;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class DashboardController {

    private static final String FXML_PACIENTES = "/pe/edu/utp/dentrix/fxml/pacientes.fxml";

    @FXML
    private TextField campoDni;

    @FXML
    private Button btnAgendarDni;

    @FXML
    private Button btnFichaDni;

    @FXML
    private Label etiquetaBusqueda;

    @FXML
    private Label kpiCitas;

    @FXML
    private Label kpiSala;

    @FXML
    private Label kpiRecaudado;

    @FXML
    private Label kpiDeudores;

    @FXML
    private Label etiquetaFechaAgenda;

    @FXML
    private TableView<Cita> tablaAgenda;

    @FXML
    private TableColumn<Cita, String> colHora;

    @FXML
    private TableColumn<Cita, String> colPaciente;

    @FXML
    private TableColumn<Cita, String> colTratamiento;

    @FXML
    private TableColumn<Cita, String> colOdontologo;

    @FXML
    private TableColumn<Cita, String> colEstado;

    private final Map<Integer, String> nombresPacientes = new HashMap<>();
    private final Map<Integer, String> nombresOdontologos = new HashMap<>();
    private final Map<Integer, String> nombresTratamientos = new HashMap<>();
    private Paciente pacienteBuscado;

    @FXML
    private void initialize() {
        colHora.setCellValueFactory(d -> new SimpleStringProperty(hora(d.getValue())));
        colPaciente.setCellValueFactory(d -> new SimpleStringProperty(
                nombresPacientes.getOrDefault(d.getValue().getPacienteId(), "—")));
        colTratamiento.setCellValueFactory(d -> new SimpleStringProperty(tratamiento(d.getValue())));
        colOdontologo.setCellValueFactory(d -> new SimpleStringProperty(
                nombresOdontologos.getOrDefault(d.getValue().getUsuarioId(), "—")));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        Badges.aplicarEn(colEstado);
        tablaAgenda.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaAgenda.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && tablaAgenda.getSelectionModel().getSelectedItem() != null) {
                Navegador.ir("/pe/edu/utp/dentrix/fxml/agenda.fxml");
            }
        });
        campoDni.setOnAction(e -> onBuscarClick());

        AppContext contexto = AppContext.get();
        if (!contexto.sesion().activa()) {
            return;
        }
        LocalDate hoy = LocalDate.now();
        etiquetaFechaAgenda.setText(hoy.format(DateTimeFormatter.ofPattern(
                "EEEE dd 'de' MMMM 'de' yyyy", new Locale("es", "PE"))));
        TareasFX.ejecutar(() -> cargarTodo(hoy),
                this::aplicarTodo,
                error -> kpiCitas.setText("—"));
    }

    private record DatosDashboard(Map<Integer, String> pacientes, Map<Integer, String> odontologos,
                                  Map<Integer, String> tratamientos, KpiDia kpi, List<Cita> citas) {
    }

    private DatosDashboard cargarTodo(LocalDate hoy) throws DentrixException {
        AppContext contexto = AppContext.get();
        Map<Integer, String> pacientes = new HashMap<>();
        for (Paciente p : contexto.pacientes().listar()) {
            pacientes.put(p.getId(), p.getApellidos() + ", " + p.getNombres());
        }
        Map<Integer, String> odontologos = new HashMap<>();
        for (Usuario u : contexto.usuarios().listarTodos()) {
            odontologos.put(u.getId(), "Dr. " + u.getNombres() + " " + u.getApellidos());
        }
        Map<Integer, String> tratamientos = new HashMap<>();
        for (Tratamiento t : contexto.catalogo().listarTratamientos()) {
            tratamientos.put(t.getId(), t.getNombre());
        }
        KpiDia kpi = contexto.reportes().kpisDeFecha(hoy);
        List<Cita> citas = contexto.agenda().agendaDelDia(hoy).stream()
                .filter(c -> "CITA".equals(c.getTipo()))
                .toList();
        return new DatosDashboard(pacientes, odontologos, tratamientos, kpi, citas);
    }

    private void aplicarTodo(DatosDashboard datos) {
        nombresPacientes.clear();
        nombresPacientes.putAll(datos.pacientes());
        nombresOdontologos.clear();
        nombresOdontologos.putAll(datos.odontologos());
        nombresTratamientos.clear();
        nombresTratamientos.putAll(datos.tratamientos());
        KpiDia kpi = datos.kpi();
        kpiCitas.setText(String.valueOf(kpi.citas()));
        kpiSala.setText(String.valueOf(kpi.enSala()));
        kpiRecaudado.setText("S/ " + String.format(Locale.US, "%,.2f", kpi.recaudado()));
        kpiDeudores.setText(String.valueOf(kpi.deudores()));
        tablaAgenda.setItems(FXCollections.observableArrayList(datos.citas()));
    }

    @FXML
    private void onBuscarClick() {
        pacienteBuscado = null;
        if (btnAgendarDni != null) btnAgendarDni.setVisible(false);
        if (btnFichaDni != null) btnFichaDni.setVisible(false);
        String dni = campoDni.getText() == null ? "" : campoDni.getText().trim();
        if (dni.isEmpty()) {
            etiquetaBusqueda.setText("Escribe un DNI para buscar (RF-04).");
            return;
        }
        if (!dni.matches("\\d{8}")) {
            etiquetaBusqueda.setText("El DNI debe tener 8 dígitos.");
            return;
        }
        try {
            Optional<Paciente> encontrado = AppContext.get().pacientes().buscarPorDni(dni);
            if (encontrado.isEmpty()) {
                etiquetaBusqueda.setText("🔍 No se encontró paciente con ese DNI — usa \"+ Registrar Nuevo Paciente\".");
            } else {
                Paciente p = encontrado.get();
                pacienteBuscado = p;
                etiquetaBusqueda.setText("✔ " + p.getCodigoHc() + " · " + p.getApellidos() + ", "
                        + p.getNombres() + " (" + p.getEstado() + ")");
                if (btnAgendarDni != null) btnAgendarDni.setVisible(true);
                if (btnFichaDni != null) btnFichaDni.setVisible(true);
            }
        } catch (DentrixException e) {
            etiquetaBusqueda.setText("Error al buscar: " + e.getMessage());
        }
    }

    @FXML
    private void onAgendarDniClick() {
        if (pacienteBuscado != null) {
            AppContext.get().sesion().setPacienteAgendable(pacienteBuscado);
            Navegador.ir("/pe/edu/utp/dentrix/fxml/agenda.fxml");
        }
    }

    @FXML
    private void onFichaDniClick() {
        if (pacienteBuscado != null) {
            AppContext.get().sesion().setPacienteEnAtencion(pacienteBuscado);
            Navegador.ir("/pe/edu/utp/dentrix/fxml/ficha-clinica.fxml");
        }
    }

    @FXML
    private void onKpiCitasClick() {
        Navegador.ir("/pe/edu/utp/dentrix/fxml/agenda.fxml");
    }

    @FXML
    private void onKpiSalaClick() {
        Navegador.ir("/pe/edu/utp/dentrix/fxml/sala-espera.fxml");
    }

    @FXML
    private void onKpiRecaudadoClick() {
        Navegador.ir("/pe/edu/utp/dentrix/fxml/caja.fxml");
    }

    @FXML
    private void onKpiDeudoresClick() {
        AppContext.get().sesion().setPestanaCajaInicial("SALDOS");
        Navegador.ir("/pe/edu/utp/dentrix/fxml/caja.fxml");
    }

    @FXML
    private void onRegistrarPacienteClick() {
        Navegador.ir(FXML_PACIENTES);
    }

    private String hora(Cita cita) {
        if (cita.getHoraInicio() == null) {
            return "";
        }
        String inicio = cita.getHoraInicio().toString();
        String fin = cita.getHoraFin() == null ? "" : cita.getHoraFin().toString();
        return inicio + " - " + fin;
    }

    private String tratamiento(Cita cita) {
        if (cita.getTratamientoId() != null) {
            return nombresTratamientos.getOrDefault(cita.getTratamientoId(), "—");
        }
        String motivo = cita.getMotivo();
        return motivo == null || motivo.isBlank() ? "—" : motivo;
    }
}
