package pe.edu.utp.dentrix.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import pe.edu.utp.dentrix.config.AppContext;
import pe.edu.utp.dentrix.dto.LineaBoleta;
import pe.edu.utp.dentrix.excepcion.DentrixException;
import pe.edu.utp.dentrix.model.CierreCaja;
import pe.edu.utp.dentrix.model.Paciente;
import pe.edu.utp.dentrix.model.Pago;
import pe.edu.utp.dentrix.model.PlanTratamiento;
import pe.edu.utp.dentrix.model.Tratamiento;
import pe.edu.utp.dentrix.model.Usuario;
import pe.edu.utp.dentrix.ui.Badges;
import pe.edu.utp.dentrix.ui.TareasFX;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CajaController {

    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final List<String> MODALIDADES = List.of("EFECTIVO", "YAPE", "PLIN", "TARJETA");
    private static final List<String> TURNOS = List.of("DIURNO", "NOCTURNO");

    public record LineaGrilla(Integer tratamientoId, String codigo, String nombre, Integer pieza,
                              Integer cantidad, BigDecimal precio, BigDecimal subtotal) {
    }

    public record FichaCobroItem(Long fichaId, LocalDateTime fecha, Integer pacienteId,
                                 String pacienteNombre, String pacienteDni, String pacienteHc,
                                 Integer odontologoId, String odontologoNombre,
                                 String resumenTratamientos, BigDecimal totalEstimado,
                                 List<PlanTratamiento> lineas) {
    }

    public record DeudorCobroItem(Long pagoId, String numeroBoleta, LocalDateTime fecha,
                                  Integer pacienteId, String pacienteNombre, String pacienteDni,
                                  BigDecimal montoTotal, BigDecimal saldoDeudor, String estadoPago) {
    }

    @FXML
    private Label etiquetaCajero;

    @FXML
    private HBox panelFichaActiva;

    @FXML
    private Label etiquetaFichaCargada;

    @FXML
    private Button btnLimpiarFicha;

    @FXML
    private ComboBox<pe.edu.utp.dentrix.model.FichaClinica> comboFichaDerivada;

    @FXML
    private ComboBox<Paciente> comboPaciente;

    @FXML
    private Label lblTotal;

    @FXML
    private ComboBox<Tratamiento> comboTratamiento;

    @FXML
    private TextField campoPieza;

    @FXML
    private TextField campoCantidad;

    @FXML
    private TableView<LineaGrilla> tablaLineas;

    @FXML
    private TableColumn<LineaGrilla, String> colTratamiento;

    @FXML
    private TableColumn<LineaGrilla, String> colPieza;

    @FXML
    private TableColumn<LineaGrilla, String> colCantidad;

    @FXML
    private TableColumn<LineaGrilla, String> colPrecio;

    @FXML
    private TableColumn<LineaGrilla, String> colSubtotal;

    @FXML
    private ComboBox<String> comboModalidad;

    @FXML
    private TextField campoOperacion;

    @FXML
    private Label etiquetaResultado;

    @FXML
    private TabPane tabPaneCaja;

    @FXML
    private Tab tabFichasPendientes;

    @FXML
    private Tab tabSaldosPendientes;

    @FXML
    private Tab tabBoletasHoy;

    @FXML
    private Label etiquetaFichasCount;

    @FXML
    private TableView<FichaCobroItem> tablaFichasPendientes;

    @FXML
    private TableColumn<FichaCobroItem, String> colFPFicha;

    @FXML
    private TableColumn<FichaCobroItem, String> colFPFecha;

    @FXML
    private TableColumn<FichaCobroItem, String> colFPPaciente;

    @FXML
    private TableColumn<FichaCobroItem, String> colFPOdontologo;

    @FXML
    private TableColumn<FichaCobroItem, String> colFPTratamientos;

    @FXML
    private TableColumn<FichaCobroItem, String> colFPTotal;

    @FXML
    private TableColumn<FichaCobroItem, Void> colFPAccion;

    @FXML
    private Label etiquetaSaldosCount;

    @FXML
    private TableView<DeudorCobroItem> tablaSaldosPendientes;

    @FXML
    private TableColumn<DeudorCobroItem, String> colSPNumero;

    @FXML
    private TableColumn<DeudorCobroItem, String> colSPFecha;

    @FXML
    private TableColumn<DeudorCobroItem, String> colSPPaciente;

    @FXML
    private TableColumn<DeudorCobroItem, String> colSPMonto;

    @FXML
    private TableColumn<DeudorCobroItem, String> colSPSaldo;

    @FXML
    private TableColumn<DeudorCobroItem, String> colSPEstado;

    @FXML
    private TableColumn<DeudorCobroItem, Void> colSPAccion;

    @FXML
    private TableView<Pago> tablaBoletas;

    @FXML
    private TableColumn<Pago, String> colBNumero;

    @FXML
    private TableColumn<Pago, String> colBFecha;

    @FXML
    private TableColumn<Pago, String> colBMonto;

    @FXML
    private TableColumn<Pago, String> colBModalidad;

    @FXML
    private TableColumn<Pago, String> colBEstado;

    @FXML
    private TableColumn<Pago, String> colBSaldo;

    @FXML
    private Label etiquetaFechaHoy;

    @FXML
    private TextField campoNumeroBoleta;

    @FXML
    private Label etiquetaDocumento;

    @FXML
    private TableView<Pago> tablaAbonos;

    @FXML
    private TableColumn<Pago, String> colAFecha;

    @FXML
    private TableColumn<Pago, String> colAMonto;

    @FXML
    private TableColumn<Pago, String> colAModalidad;

    @FXML
    private TableColumn<Pago, String> colAOp;

    @FXML
    private TextField campoMontoAbono;

    @FXML
    private ComboBox<String> comboModalidadAbono;

    @FXML
    private TextField campoOpAbono;

    @FXML
    private Label etiquetaAbonoResultado;

    @FXML
    private ComboBox<String> comboTurno;

    @FXML
    private TextField campoFondo;

    @FXML
    private Label etiquetaSinCierre;

    @FXML
    private Label etiquetaCierreInfo;

    @FXML
    private TextField campoNuevoFondo;

    @FXML
    private TextField campoContado;

    @FXML
    private Label etiquetaCierreResultado;

    @FXML
    private HBox panelCaja;

    @FXML
    private VBox panelCierre;

    private final ObservableList<LineaGrilla> lineas = FXCollections.observableArrayList();
    private final List<Paciente> pacientesActivos = new ArrayList<>();
    private final List<Tratamiento> tratamientosActivos = new ArrayList<>();
    private Pago documentoAbonado;
    private CierreCaja cierreActual;
    private Integer usuarioCajaId;
    private Long fichaCobroId;
    private Integer odontologoCobroId;

    @FXML
    private void initialize() {
        colTratamiento.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().codigo() + " " + d.getValue().nombre()));
        colPieza.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().pieza() == null ? "" : String.valueOf(d.getValue().pieza())));
        colCantidad.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().cantidad())));
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().precio().toPlainString()));
        colSubtotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().subtotal().toPlainString()));
        tablaLineas.setItems(lineas);

        colBNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colBFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFecha() == null ? "" : FECHA_CORTA.format(d.getValue().getFecha())));
        colBMonto.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getMonto() == null ? "" : d.getValue().getMonto().toPlainString()));
        colBModalidad.setCellValueFactory(new PropertyValueFactory<>("modalidad"));
        colBEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
        pe.edu.utp.dentrix.ui.Badges.aplicarEn(colBEstado);
        colBSaldo.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getSaldoDeudor() == null ? "" : d.getValue().getSaldoDeudor().toPlainString()));

        colAFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFecha() == null ? "" : FECHA_CORTA.format(d.getValue().getFecha())));
        colAMonto.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getMonto() == null ? "" : d.getValue().getMonto().toPlainString()));
        colAModalidad.setCellValueFactory(new PropertyValueFactory<>("modalidad"));
        colAOp.setCellValueFactory(new PropertyValueFactory<>("operacionRef"));

        comboModalidad.setItems(FXCollections.observableArrayList(MODALIDADES));
        comboModalidadAbono.setItems(FXCollections.observableArrayList(MODALIDADES));
        comboTurno.setItems(FXCollections.observableArrayList(TURNOS));

        comboPaciente.setConverter(new StringConverter<Paciente>() {
            @Override
            public String toString(Paciente p) {
                return p == null ? "" : p.getApellidos() + ", " + p.getNombres() + " (DNI " + p.getDni() + ")";
            }

            @Override
            public Paciente fromString(String s) {
                return null;
            }
        });
        comboTratamiento.setConverter(new StringConverter<Tratamiento>() {
            @Override
            public String toString(Tratamiento t) {
                return t == null ? "" : t.getCodigo() + " - " + t.getNombre() + " S/ " + t.getPrecio().toPlainString();
            }

            @Override
            public Tratamiento fromString(String s) {
                return null;
            }
        });

        if (comboFichaDerivada != null) {
            comboFichaDerivada.setConverter(new StringConverter<>() {
                @Override
                public String toString(pe.edu.utp.dentrix.model.FichaClinica f) {
                    return f == null ? "" : "Ficha #" + f.getId() + " (Paciente #" + f.getPacienteId() + ") - " + f.getEstadoDerivacion();
                }

                @Override
                public pe.edu.utp.dentrix.model.FichaClinica fromString(String string) {
                    return null;
                }
            });
        }

        if (tablaFichasPendientes != null) {
            colFPFicha.setCellValueFactory(d -> new SimpleStringProperty("#" + d.getValue().fichaId()));
            colFPFecha.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().fecha() == null ? "" : FECHA_CORTA.format(d.getValue().fecha())));
            colFPPaciente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().pacienteNombre()));
            colFPOdontologo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().odontologoNombre()));
            colFPTratamientos.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().resumenTratamientos()));
            colFPTotal.setCellValueFactory(d -> new SimpleStringProperty(
                    "S/ " + d.getValue().totalEstimado().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()));
            colFPAccion.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("💳 Cobrar");
                {
                    btn.getStyleClass().addAll("button", "boton-primario", "boton-mini");
                    btn.setOnAction(e -> {
                        FichaCobroItem item = getTableRow().getItem();
                        if (item != null) {
                            cargarFichaCobroItem(item);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean vacio) {
                    super.updateItem(item, vacio);
                    setGraphic(vacio ? null : btn);
                }
            });
            tablaFichasPendientes.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && tablaFichasPendientes.getSelectionModel().getSelectedItem() != null) {
                    cargarFichaCobroItem(tablaFichasPendientes.getSelectionModel().getSelectedItem());
                }
            });
        }

        if (tablaSaldosPendientes != null) {
            colSPNumero.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().numeroBoleta()));
            colSPFecha.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().fecha() == null ? "" : FECHA_CORTA.format(d.getValue().fecha())));
            colSPPaciente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().pacienteNombre()));
            colSPMonto.setCellValueFactory(d -> new SimpleStringProperty(
                    "S/ " + d.getValue().montoTotal().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()));
            colSPSaldo.setCellValueFactory(d -> new SimpleStringProperty(
                    "S/ " + d.getValue().saldoDeudor().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()));
            colSPEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estadoPago()));
            Badges.aplicarEn(colSPEstado);
            colSPAccion.setCellFactory(col -> new TableCell<>() {
                private final Button btn = new Button("💵 Cobrar Saldo");
                {
                    btn.getStyleClass().addAll("button", "boton-mini");
                    btn.setOnAction(e -> {
                        DeudorCobroItem item = getTableRow().getItem();
                        if (item != null) {
                            cargarDeudorCobro(item);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean vacio) {
                    super.updateItem(item, vacio);
                    setGraphic(vacio ? null : btn);
                }
            });
            tablaSaldosPendientes.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && tablaSaldosPendientes.getSelectionModel().getSelectedItem() != null) {
                    cargarDeudorCobro(tablaSaldosPendientes.getSelectionModel().getSelectedItem());
                }
            });
        }

        lineas.addListener((javafx.collections.ListChangeListener<LineaGrilla>) c -> actualizarTotal());

        AppContext contexto = AppContext.get();
        if (!contexto.sesion().activa()) {
            return;
        }
        usuarioCajaId = contexto.sesion().getUsuario().getId();
        etiquetaCajero.setText("Cajero: " + contexto.sesion().getNombreCompleto());
        comboModalidad.setValue("EFECTIVO");
        comboModalidadAbono.setValue("EFECTIVO");
        comboTurno.setValue("DIURNO");
        etiquetaFechaHoy.setText("Hoy: " + LocalDate.now());
        TareasFX.ejecutar(this::cargarCombos,
                this::aplicarCombos,
                error -> mostrarError(TareasFX.mensaje(error)));
        recargarBoletas();
        recargarCierre();
        recargarFichasPendientes();
        recargarSaldosPendientes();

        String pestana = AppContext.get().sesion().getPestanaCajaInicial();
        if ("SALDOS".equalsIgnoreCase(pestana) && tabPaneCaja != null && tabSaldosPendientes != null) {
            tabPaneCaja.getSelectionModel().select(tabSaldosPendientes);
            AppContext.get().sesion().setPestanaCajaInicial(null);
        }
    }

    private record DatosCombos(List<Paciente> pacientesActivos, List<Tratamiento> tratamientosActivos,
                               List<pe.edu.utp.dentrix.model.FichaClinica> fichasCobrables) {
    }

    private DatosCombos cargarCombos() throws DentrixException {
        AppContext contexto = AppContext.get();
        List<Paciente> activos = new ArrayList<>();
        for (Paciente p : contexto.pacientes().listar()) {
            if ("ACTIVO".equals(p.getEstado())) {
                activos.add(p);
            }
        }
        return new DatosCombos(activos, contexto.catalogo().listarTratamientosActivos(),
                contexto.fichas().listarCobrables());
    }

    private void aplicarCombos(DatosCombos datos) {
        pacientesActivos.clear();
        pacientesActivos.addAll(datos.pacientesActivos());
        tratamientosActivos.clear();
        tratamientosActivos.addAll(datos.tratamientosActivos());
        comboPaciente.setItems(FXCollections.observableArrayList(pacientesActivos));
        comboTratamiento.setItems(FXCollections.observableArrayList(tratamientosActivos));
        if (comboFichaDerivada != null) {
            comboFichaDerivada.setItems(FXCollections.observableArrayList(datos.fichasCobrables()));
            var fichaPendiente = AppContext.get().sesion().getFichaCobro();
            if (fichaPendiente != null) {
                for (var f : datos.fichasCobrables()) {
                    if (f.getId().equals(fichaPendiente.getId())) {
                        comboFichaDerivada.setValue(f);
                        cargarFichaDerivada(f);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    private void onCargarFichaDerivadaClick() {
        if (comboFichaDerivada == null || comboFichaDerivada.getValue() == null) {
            mostrarInfo("Selecciona primero una ficha derivada de la lista.");
            return;
        }
        cargarFichaDerivada(comboFichaDerivada.getValue());
    }

    private void cargarFichaDerivada(pe.edu.utp.dentrix.model.FichaClinica ficha) {
        if (ficha == null) {
            return;
        }
        fichaCobroId = ficha.getId();
        odontologoCobroId = ficha.getOdontologoId();
        for (Paciente p : pacientesActivos) {
            if (p.getId().equals(ficha.getPacienteId())) {
                comboPaciente.setValue(p);
                break;
            }
        }
        TareasFX.ejecutar(() -> AppContext.get().fichas().planPorFicha(ficha.getId()),
                planes -> {
                    lineas.clear();
                    for (var plan : planes) {
                        Tratamiento t = null;
                        for (Tratamiento tr : tratamientosActivos) {
                            if (tr.getId().equals(plan.getTratamientoId())) {
                                t = tr;
                                break;
                            }
                        }
                        if (t != null) {
                            int cant = plan.getCantidad() != null ? plan.getCantidad() : 1;
                            BigDecimal precio = plan.getPrecioUnitario() != null ? plan.getPrecioUnitario() : t.getPrecio();
                            lineas.add(new LineaGrilla(t.getId(), t.getCodigo(), t.getNombre(),
                                    plan.getPiezaFdi(), cant, precio, precio.multiply(BigDecimal.valueOf(cant))));
                        }
                    }
                    indicar(etiquetaResultado, "Ficha #" + ficha.getId() + " cargada con " + lineas.size() + " tratamiento(s).", false);
                    if (panelFichaActiva != null) {
                        panelFichaActiva.setVisible(true);
                        panelFichaActiva.setManaged(true);
                    }
                    if (etiquetaFichaCargada != null) {
                        etiquetaFichaCargada.setText("✔ Ficha Clínica #" + ficha.getId() + " vinculada para cobro.");
                    }
                },
                error -> mostrarError(TareasFX.mensaje(error)));
    }

    @FXML
    private void onLimpiarFichaClick() {
        fichaCobroId = null;
        odontologoCobroId = null;
        AppContext.get().sesion().setFichaCobro(null);
        if (panelFichaActiva != null) {
            panelFichaActiva.setVisible(false);
            panelFichaActiva.setManaged(false);
        }
        if (comboFichaDerivada != null) {
            comboFichaDerivada.setValue(null);
        }
        lineas.clear();
        campoOperacion.clear();
        comboPaciente.setValue(null);
        indicar(etiquetaResultado, "Modo cobro libre. Selecciona un paciente y tratamientos del tarifario.", false);
    }

    @FXML
    private void onRefrescarFichasClick() {
        recargarFichasPendientes();
    }

    @FXML
    private void onRefrescarSaldosClick() {
        recargarSaldosPendientes();
    }

    private void cargarFichaCobroItem(FichaCobroItem item) {
        if (item == null) return;
        fichaCobroId = item.fichaId();
        odontologoCobroId = item.odontologoId();
        Paciente encontrado = null;
        for (Paciente p : comboPaciente.getItems()) {
            if (p.getId().equals(item.pacienteId())) {
                encontrado = p;
                break;
            }
        }
        if (encontrado == null) {
            try {
                encontrado = AppContext.get().pacientes().buscarPorId(item.pacienteId());
                if (encontrado != null) {
                    comboPaciente.getItems().add(encontrado);
                }
            } catch (Exception ignorada) {
            }
        }
        if (encontrado != null) {
            comboPaciente.setValue(encontrado);
        }

        lineas.clear();
        for (var plan : item.lineas()) {
            Tratamiento t = null;
            for (Tratamiento tr : tratamientosActivos) {
                if (tr.getId().equals(plan.getTratamientoId())) {
                    t = tr;
                    break;
                }
            }
            if (t == null) {
                try {
                    t = AppContext.get().catalogo().buscarPorId(plan.getTratamientoId()).orElse(null);
                } catch (Exception ignorada) {
                }
            }
            if (t != null) {
                int cant = plan.getCantidad() != null ? plan.getCantidad() : 1;
                BigDecimal precio = plan.getPrecioUnitario() != null ? plan.getPrecioUnitario() : t.getPrecio();
                lineas.add(new LineaGrilla(t.getId(), t.getCodigo(), t.getNombre(),
                        plan.getPiezaFdi(), cant, precio, precio.multiply(BigDecimal.valueOf(cant))));
            }
        }
        if (panelFichaActiva != null) {
            panelFichaActiva.setVisible(true);
            panelFichaActiva.setManaged(true);
        }
        if (etiquetaFichaCargada != null) {
            etiquetaFichaCargada.setText("✔ Ficha #" + item.fichaId() + " · " + item.pacienteNombre()
                    + " · Dr. " + item.odontologoNombre() + " · Total: S/ " + item.totalEstimado().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        }
        indicar(etiquetaResultado, "✔ Ficha #" + item.fichaId() + " cargada con " + lineas.size()
                + " tratamiento(s). Total: S/ " + item.totalEstimado().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
                + ". Selecciona la modalidad y emite la boleta.", false);
        comboModalidad.requestFocus();
    }

    private void cargarDeudorCobro(DeudorCobroItem item) {
        if (item == null) return;
        campoNumeroBoleta.setText(item.numeroBoleta());
        onCargarBoletaClick();
        campoMontoAbono.setText(item.saldoDeudor().toPlainString());
        campoMontoAbono.requestFocus();
    }

    private void recargarFichasPendientes() {
        TareasFX.ejecutar(() -> {
            AppContext ctx = AppContext.get();
            List<pe.edu.utp.dentrix.model.FichaClinica> fichas = ctx.fichas().listarCobrables();
            Map<Integer, Paciente> pacs = new HashMap<>();
            for (Paciente p : ctx.pacientes().listar()) {
                pacs.put(p.getId(), p);
            }
            Map<Integer, Usuario> odos = new HashMap<>();
            for (Usuario u : ctx.usuarios().listarTodos()) {
                odos.put(u.getId(), u);
            }
            Map<Integer, Tratamiento> trats = new HashMap<>();
            for (Tratamiento t : ctx.catalogo().listarTratamientos()) {
                trats.put(t.getId(), t);
            }
            List<FichaCobroItem> items = new ArrayList<>();
            for (var f : fichas) {
                Paciente pac = pacs.get(f.getPacienteId());
                Usuario odo = odos.get(f.getOdontologoId());
                List<PlanTratamiento> plan = ctx.fichas().planPorFicha(f.getId());
                BigDecimal total = BigDecimal.ZERO;
                StringBuilder sb = new StringBuilder();
                for (var pt : plan) {
                    Tratamiento t = trats.get(pt.getTratamientoId());
                    BigDecimal precio = pt.getPrecioUnitario() != null ? pt.getPrecioUnitario() : (t != null ? t.getPrecio() : BigDecimal.ZERO);
                    int cant = pt.getCantidad() != null ? pt.getCantidad() : 1;
                    total = total.add(precio.multiply(BigDecimal.valueOf(cant)));
                    if (!sb.isEmpty()) sb.append(", ");
                    sb.append(t != null ? t.getNombre() : "Tratamiento #" + pt.getTratamientoId());
                    if (pt.getPiezaFdi() != null) sb.append(" (Pz ").append(pt.getPiezaFdi()).append(")");
                    if (cant > 1) sb.append(" x").append(cant);
                }
                String nombrePac = pac != null ? pac.getApellidos() + ", " + pac.getNombres() : "Paciente #" + f.getPacienteId();
                String dniPac = pac != null ? pac.getDni() : "—";
                String hcPac = pac != null ? pac.getCodigoHc() : "—";
                String nombreOdo = odo != null ? "Dr. " + odo.getNombres() + " " + odo.getApellidos() : "—";
                items.add(new FichaCobroItem(f.getId(), f.getFechaDerivacion() != null ? f.getFechaDerivacion() : f.getFecha(),
                        f.getPacienteId(), nombrePac, dniPac, hcPac, f.getOdontologoId(), nombreOdo,
                        sb.isEmpty() ? "—" : sb.toString(), total, plan));
            }
            return items;
        }, items -> {
            if (tablaFichasPendientes != null) {
                tablaFichasPendientes.setItems(FXCollections.observableArrayList(items));
            }
            if (etiquetaFichasCount != null) {
                etiquetaFichasCount.setText(items.size() + " paciente(s) por cobrar");
            }
        }, error -> mostrarError(TareasFX.mensaje(error)));
    }

    private void recargarSaldosPendientes() {
        TareasFX.ejecutar(() -> {
            AppContext ctx = AppContext.get();
            List<Pago> pagosConSaldo = ctx.caja().documentosConSaldo();
            Map<Integer, Paciente> pacs = new HashMap<>();
            for (Paciente p : ctx.pacientes().listar()) {
                pacs.put(p.getId(), p);
            }
            List<DeudorCobroItem> deudores = new ArrayList<>();
            for (Pago p : pagosConSaldo) {
                Paciente pac = pacs.get(p.getPacienteId());
                String nombrePac = pac != null ? pac.getApellidos() + ", " + pac.getNombres() : "Paciente #" + p.getPacienteId();
                String dniPac = pac != null ? pac.getDni() : "—";
                deudores.add(new DeudorCobroItem(p.getId(), p.getNumero(), p.getFecha(),
                        p.getPacienteId(), nombrePac, dniPac, p.getMonto(), p.getSaldoDeudor(), p.getEstadoPago()));
            }
            return deudores;
        }, deudores -> {
            if (tablaSaldosPendientes != null) {
                tablaSaldosPendientes.setItems(FXCollections.observableArrayList(deudores));
            }
            if (etiquetaSaldosCount != null) {
                etiquetaSaldosCount.setText(deudores.size() + " boleta(s) con saldo");
            }
        }, error -> mostrarError(TareasFX.mensaje(error)));
    }

    private void actualizarTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LineaGrilla linea : lineas) {
            total = total.add(linea.subtotal());
        }
        lblTotal.setText("S/ " + total.toPlainString());
    }

    @FXML
    private void onAgregarLineaClick() {
        try {
            Tratamiento tratamiento = comboTratamiento.getValue();
            if (tratamiento == null) {
                throw new DentrixException("Selecciona un tratamiento del tarifario");
            }
            String textoCantidad = campoCantidad.getText() == null ? "" : campoCantidad.getText().trim();
            int cantidad = textoCantidad.isEmpty() ? 1 : Integer.parseInt(textoCantidad);
            if (cantidad < 1) {
                throw new DentrixException("La cantidad debe ser al menos 1");
            }
            Integer pieza = null;
            String textoPieza = campoPieza.getText() == null ? "" : campoPieza.getText().trim();
            if (!textoPieza.isEmpty()) {
                if (!textoPieza.matches("\\d{1,2}")) {
                    throw new DentrixException("La pieza FDI debe ser un número de 1 o 2 dígitos (ej. 21, 48)");
                }
                pieza = Integer.parseInt(textoPieza);
            }
            BigDecimal precio = tratamiento.getPrecio();
            lineas.add(new LineaGrilla(tratamiento.getId(), tratamiento.getCodigo(), tratamiento.getNombre(),
                    pieza, cantidad, precio, precio.multiply(BigDecimal.valueOf(cantidad))));
            campoPieza.clear();
            campoCantidad.setText("1");
            etiquetaResultado.setText("");
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero");
        } catch (DentrixException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onQuitarLineaClick() {
        LineaGrilla seleccionada = tablaLineas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarInfo("Selecciona una línea de la tabla para quitarla.");
            return;
        }
        lineas.remove(seleccionada);
    }

    @FXML
    private void onEmitirClick() {
        try {
            Paciente paciente = comboPaciente.getValue();
            if (paciente == null) {
                throw new DentrixException("Selecciona el paciente de la boleta");
            }
            if (lineas.isEmpty()) {
                throw new DentrixException("Agrega al menos un tratamiento antes de emitir");
            }
            String modalidad = comboModalidad.getValue();
            String operacion = texto(campoOperacion);
            if (!"EFECTIVO".equals(modalidad) && operacion == null) {
                throw new DentrixException("RF-08: Yape/Plin/Tarjeta requieren N° de operación");
            }
            List<LineaBoleta> detalle = new ArrayList<>();
            for (LineaGrilla g : lineas) {
                detalle.add(new LineaBoleta(g.tratamientoId(), g.pieza(), g.cantidad()));
            }
            Integer pacienteId = paciente.getId();
            Long cierreId = cierreActual != null ? cierreActual.getId() : null;
            TareasFX.ejecutar(() -> AppContext.get().caja().emitirBoleta(pacienteId, modalidad, operacion,
                            fichaCobroId, odontologoCobroId, usuarioCajaId, cierreId, detalle),
                    boleta -> {
                        indicar(etiquetaResultado, "✔ Boleta " + boleta.getNumero() + " emitida correctamente: Total S/ "
                                + boleta.getMonto().toPlainString()
                                + (boleta.getSaldoDeudor() != null && boleta.getSaldoDeudor().compareTo(BigDecimal.ZERO) > 0
                                ? " (Saldo: S/ " + boleta.getSaldoDeudor().toPlainString() + ")" : ""), false);

                        StringBuilder voucher = new StringBuilder();
                        voucher.append("=====================================================\n");
                        voucher.append("        COMPROBANTE DE COBRO — DENTRIX DENTAL         \n");
                        voucher.append("=====================================================\n");
                        voucher.append("Boleta N°: ").append(boleta.getNumero()).append("\n");
                        if (boleta.getFecha() != null) {
                            voucher.append("Fecha/Hora: ").append(FECHA_CORTA.format(boleta.getFecha())).append("\n");
                        }
                        voucher.append("Paciente: ").append(paciente.getApellidos()).append(", ").append(paciente.getNombres())
                                .append(" (DNI: ").append(paciente.getDni()).append(" · HC: ").append(paciente.getCodigoHc()).append(")\n");
                        voucher.append("Modalidad: ").append(boleta.getModalidad());
                        if (boleta.getOperacionRef() != null && !boleta.getOperacionRef().isBlank()) {
                            voucher.append(" (N° Operación: ").append(boleta.getOperacionRef()).append(")");
                        }
                        voucher.append("\n-----------------------------------------------------\n");
                        voucher.append("TOTAL COBRADO: S/ ").append(boleta.getMonto().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()).append("\n");
                        if (boleta.getSaldoDeudor() != null && boleta.getSaldoDeudor().compareTo(BigDecimal.ZERO) > 0) {
                            voucher.append("⚠️ Saldo Deudor Pendiente: S/ ")
                                    .append(boleta.getSaldoDeudor().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()).append("\n");
                            voucher.append("Estado de pago: ").append(boleta.getEstadoPago()).append("\n");
                            voucher.append("Nota: El saldo se derivó automáticamente a 'Saldos por Cobrar'.\n");
                        } else {
                            voucher.append("Estado de pago: CANCELADO TOTAL (S/ 0.00 pendiente)\n");
                        }
                        voucher.append("=====================================================\n");
                        voucher.append("✔ Cobro registrado en el sistema y asentado en caja.");

                        lineas.clear();
                        campoOperacion.clear();
                        fichaCobroId = null;
                        odontologoCobroId = null;
                        if (comboFichaDerivada != null) {
                            comboFichaDerivada.setValue(null);
                        }
                        if (panelFichaActiva != null) {
                            panelFichaActiva.setVisible(false);
                            panelFichaActiva.setManaged(false);
                        }
                        AppContext.get().sesion().setFichaCobro(null);
                        recargarBoletas();
                        recargarFichasPendientes();
                        recargarSaldosPendientes();

                        if (tabPaneCaja != null && tabBoletasHoy != null) {
                            tabPaneCaja.getSelectionModel().select(tabBoletasHoy);
                        }

                        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                        alerta.setTitle("Boleta Emitida Exitosamente");
                        alerta.setHeaderText("Cobro Registrado: " + boleta.getNumero());
                        alerta.setContentText(voucher.toString());
                        alerta.showAndWait();
                    },
                    error -> mostrarError(TareasFX.mensaje(error)));
        } catch (DentrixException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onRefrescarBoletasClick() {
        recargarBoletas();
    }

    private void recargarBoletas() {
        TareasFX.ejecutar(() -> AppContext.get().caja().boletasDelDia(LocalDate.now()),
                boletas -> tablaBoletas.setItems(FXCollections.observableArrayList(boletas)),
                error -> mostrarError(TareasFX.mensaje(error)));
    }

    @FXML
    private void onCargarBoletaClick() {
        String numero;
        try {
            numero = texto(campoNumeroBoleta);
            if (numero == null) {
                throw new DentrixException("Ingresa el número de la boleta (ej. B001-000440)");
            }
        } catch (DentrixException e) {
            documentoAbonado = null;
            etiquetaDocumento.setText(e.getMessage());
            tablaAbonos.setItems(FXCollections.observableArrayList());
            return;
        }
        TareasFX.ejecutar(() -> {
            var caja = AppContext.get().caja();
            Pago boleta = caja.buscarBoleta(numero);
            return new DocumentoAbonado(boleta, caja.abonosDe(boleta.getId()));
        }, datos -> {
            documentoAbonado = datos.boleta();
            mostrarDocumentoAbonado();
            tablaAbonos.setItems(FXCollections.observableArrayList(datos.abonos()));
            if (datos.boleta() != null && datos.boleta().getSaldoDeudor() != null
                    && datos.boleta().getSaldoDeudor().compareTo(BigDecimal.ZERO) > 0) {
                campoMontoAbono.setText(datos.boleta().getSaldoDeudor().toPlainString());
            }
            etiquetaAbonoResultado.setText("");
        }, error -> {
            documentoAbonado = null;
            etiquetaDocumento.setText(TareasFX.mensaje(error));
            tablaAbonos.setItems(FXCollections.observableArrayList());
        });
    }

    private record DocumentoAbonado(Pago boleta, List<Pago> abonos) {
    }

    private void mostrarDocumentoAbonado() {
        etiquetaDocumento.setText("Boleta " + documentoAbonado.getNumero()
                + "  |  Monto S/ " + documentoAbonado.getMonto().toPlainString()
                + "  |  Saldo S/ " + documentoAbonado.getSaldoDeudor().toPlainString()
                + "  |  Estado pago: " + documentoAbonado.getEstadoPago());
    }

    @FXML
    private void onRegistrarAbonoClick() {
        try {
            if (documentoAbonado == null) {
                throw new DentrixException("Carga primero la boleta a la que abonar");
            }
            BigDecimal monto;
            try {
                monto = new BigDecimal(texto(campoMontoAbono) == null ? "" : texto(campoMontoAbono));
            } catch (NumberFormatException e) {
                throw new DentrixException("El monto del abono debe ser un número (ej. 150.00)");
            }
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new DentrixException("El monto del abono debe ser mayor a 0");
            }
            String modalidad = comboModalidadAbono.getValue();
            String operacion = texto(campoOpAbono);
            if (!"EFECTIVO".equals(modalidad) && operacion == null) {
                throw new DentrixException("RF-08: Yape/Plin/Tarjeta requieren N° de operación");
            }
            Long documentoId = documentoAbonado.getId();
            String numeroBoleta = documentoAbonado.getNumero();
            TareasFX.ejecutar(() -> {
                var caja = AppContext.get().caja();
                caja.registrarAbono(documentoId, monto, modalidad, operacion, usuarioCajaId);
                Pago actualizado = caja.buscarBoleta(numeroBoleta);
                return new DocumentoAbonado(actualizado, caja.abonosDe(actualizado.getId()));
            }, datos -> {
                documentoAbonado = datos.boleta();
                mostrarDocumentoAbonado();
                tablaAbonos.setItems(FXCollections.observableArrayList(datos.abonos()));
                campoMontoAbono.clear();
                campoOpAbono.clear();
                indicar(etiquetaAbonoResultado, "✔ Abono de S/ " + monto.toPlainString() + " registrado correctamente para " + numeroBoleta, false);
                recargarBoletas();
                recargarSaldosPendientes();
                mostrarInfo("✔ Abono de S/ " + monto.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
                        + " registrado con éxito para la boleta " + numeroBoleta + ".\n"
                        + "Nuevo saldo pendiente: S/ " + (datos.boleta() != null && datos.boleta().getSaldoDeudor() != null
                        ? datos.boleta().getSaldoDeudor().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() : "0.00")
                        + " (Estado: " + (datos.boleta() != null ? datos.boleta().getEstadoPago() : "CANCELADO") + ")");
            }, error -> mostrarError(TareasFX.mensaje(error)));
        } catch (DentrixException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onAbrirCierreClick() {
        try {
            BigDecimal fondo;
            try {
                fondo = new BigDecimal(texto(campoFondo) == null ? "" : texto(campoFondo));
            } catch (NumberFormatException e) {
                throw new DentrixException("El fondo inicial debe ser un número (ej. 100.00)");
            }
            if (fondo.compareTo(BigDecimal.ZERO) < 0) {
                throw new DentrixException("El fondo inicial no puede ser negativo");
            }
            LocalDate hoy = LocalDate.now();
            String turno = comboTurno.getValue();
            TareasFX.ejecutar(() -> AppContext.get().caja().abrirCierre(hoy, turno, usuarioCajaId, fondo),
                    cierre -> {
                        cierreActual = cierre;
                        campoFondo.clear();
                        indicar(etiquetaCierreResultado, "Cierre de turno " + cierre.getTurno() + " abierto", false);
                        recargarCierre();
                    },
                    error -> mostrarError(TareasFX.mensaje(error)));
        } catch (DentrixException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onCambiarFondoClick() {
        try {
            if (cierreActual == null || !"ABIERTO".equals(cierreActual.getEstado())) {
                throw new DentrixException("Solo se cambia el fondo de un cierre en estado ABIERTO");
            }
            BigDecimal fondo;
            try {
                fondo = new BigDecimal(texto(campoNuevoFondo) == null ? "" : texto(campoNuevoFondo));
            } catch (NumberFormatException e) {
                throw new DentrixException("El nuevo fondo debe ser un número (ej. 150.00)");
            }
            if (fondo.compareTo(BigDecimal.ZERO) < 0) {
                throw new DentrixException("El fondo inicial no puede ser negativo");
            }
            Long cierreId = cierreActual.getId();
            TareasFX.ejecutar(() -> AppContext.get().caja().cambiarFondo(cierreId, fondo),
                    cierre -> {
                        cierreActual = cierre;
                        campoNuevoFondo.clear();
                        indicar(etiquetaCierreResultado, "Fondo inicial actualizado a S/ " + fondo.toPlainString(), false);
                        recargarCierre();
                    },
                    error -> mostrarError(TareasFX.mensaje(error)));
        } catch (DentrixException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onConsolidarClick() {
        try {
            if (cierreActual == null || !"ABIERTO".equals(cierreActual.getEstado())) {
                throw new DentrixException("Solo se consolida un cierre en estado ABIERTO");
            }
            BigDecimal contado;
            try {
                contado = new BigDecimal(texto(campoContado) == null ? "" : texto(campoContado));
            } catch (NumberFormatException e) {
                throw new DentrixException("El efectivo contado debe ser un número (ej. 350.00)");
            }
            Long cierreId = cierreActual.getId();
            TareasFX.ejecutar(() -> AppContext.get().caja().consolidar(cierreId, contado, usuarioCajaId),
                    cierre -> {
                        cierreActual = cierre;
                        campoContado.clear();
                        indicar(etiquetaCierreResultado, "Cierre consolidado con diferencia S/ "
                                + cierre.getDiferencia().toPlainString(), false);
                        recargarCierre();
                    },
                    error -> mostrarError(TareasFX.mensaje(error)));
        } catch (DentrixException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onActaClick() {
        try {
            if (cierreActual == null) {
                throw new DentrixException("No hay cierre del día para marcar el acta");
            }
            Long cierreId = cierreActual.getId();
            LocalDate hoy = LocalDate.now();
            TareasFX.ejecutar(() -> {
                var caja = AppContext.get().caja();
                caja.marcarActaGenerada(cierreId);
                return caja.buscarCierre(hoy);
            }, cierre -> {
                cierreActual = cierre;
                indicar(etiquetaCierreResultado, "Acta de arqueo marcada como generada", false);
                recargarCierre();
            }, error -> mostrarError(TareasFX.mensaje(error)));
        } catch (DentrixException e) {
            mostrarError(e.getMessage());
        }
    }

    private void recargarCierre() {
        TareasFX.ejecutar(this::buscarCierreDelDia,
                this::aplicarCierre,
                error -> mostrarError(TareasFX.mensaje(error)));
    }

    private CierreCaja buscarCierreDelDia() {
        try {
            return AppContext.get().caja().buscarCierre(LocalDate.now());
        } catch (DentrixException e) {
            return null;
        }
    }

    private void aplicarCierre(CierreCaja cierre) {
        cierreActual = cierre;
        if (cierreActual == null) {
            etiquetaSinCierre.setText("Todavía no se abrió el cierre del día. Abre el turno con su fondo inicial.");
            etiquetaCierreInfo.setText("");
            return;
        }
        etiquetaSinCierre.setText("");
        StringBuilder info = new StringBuilder();
        info.append("Fecha: ").append(cierreActual.getFecha())
                .append("   Turno: ").append(cierreActual.getTurno())
                .append("   Estado: ").append(cierreActual.getEstado()).append('\n');
        info.append("Fondo inicial: S/ ").append(cierreActual.getFondoInicial().toPlainString()).append('\n');
        info.append("Historial de fondos: ").append(cierreActual.getHistorialFondos()).append('\n');
        info.append("Efectivo en sistema: S/ ").append(cierreActual.getEfectivoSistema().toPlainString()).append('\n');
        info.append("Billeteras digitales: S/ ").append(cierreActual.getBilleterasSistema().toPlainString()).append('\n');
        info.append("Total sistema: S/ ").append(cierreActual.getTotalSistema().toPlainString());
        if (cierreActual.getEfectivoContado() != null) {
            info.append('\n');
            info.append("Efectivo contado: S/ ").append(cierreActual.getEfectivoContado().toPlainString()).append('\n');
            info.append("Diferencia: S/ ").append(cierreActual.getDiferencia().toPlainString());
        }
        if (cierreActual.getConsolidadoEn() != null) {
            info.append('\n');
            info.append("Consolidado el ").append(FECHA_CORTA.format(cierreActual.getConsolidadoEn()));
        }
        info.append('\n');
        info.append("Acta generada: ").append(cierreActual.isActaGenerada() ? "Sí" : "No");
        etiquetaCierreInfo.setText(info.toString());
    }

    private String texto(TextField campo) {
        String valor = campo.getText() == null ? "" : campo.getText().trim();
        return valor.isEmpty() ? null : valor;
    }

    private void indicar(Label etiqueta, String mensaje, boolean error) {
        etiqueta.setText(mensaje);
        etiqueta.getStyleClass().removeAll("etiqueta-error", "etiqueta-ok");
        etiqueta.getStyleClass().add(error ? "etiqueta-error" : "etiqueta-ok");
    }

    @FXML
    private void onVolverClick() {
        pe.edu.utp.dentrix.ui.Navegador.ir(pe.edu.utp.dentrix.ui.Navegador.INICIO);
    }

    @FXML
    private void onCierreClick() {
        panelCaja.setVisible(false);
        panelCaja.setManaged(false);
        panelCierre.setVisible(true);
        panelCierre.setManaged(true);
    }

    @FXML
    private void onCajaClick() {
        panelCaja.setVisible(true);
        panelCaja.setManaged(true);
        panelCierre.setVisible(false);
        panelCierre.setManaged(false);
    }

    private void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Caja");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Caja");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
