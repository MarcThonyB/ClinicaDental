package pe.edu.utp.dentrix.service;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.utp.dentrix.dao.impl.ReporteDAOImpl;
import pe.edu.utp.dentrix.dao.PagoDAO;
import pe.edu.utp.dentrix.dto.KpiDia;
import pe.edu.utp.dentrix.dto.KpiMes;
import pe.edu.utp.dentrix.dto.ProductividadOdontologo;
import pe.edu.utp.dentrix.dto.RankingTratamiento;
import pe.edu.utp.dentrix.dto.SaldoPaciente;
import pe.edu.utp.dentrix.excepcion.DentrixException;
import pe.edu.utp.dentrix.model.Pago;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

    private final ReporteDAOImpl reporteDAO;
    private final PagoDAO pagoDAO;

    public ReporteService(ReporteDAOImpl reporteDAO, PagoDAO pagoDAO) {
        this.reporteDAO = Preconditions.checkNotNull(reporteDAO, "reporteDAO no puede ser nulo");
        this.pagoDAO = Preconditions.checkNotNull(pagoDAO, "pagoDAO no puede ser nulo");
    }

    public KpiDia kpisDeFecha(LocalDate fecha) throws DentrixException {
        if (fecha == null) {
            throw new DentrixException("La fecha del reporte de KPI es obligatoria");
        }
        return reporteDAO.kpisDeFecha(fecha);
    }

    public KpiMes kpisDelMes(LocalDate desde, LocalDate hasta) throws DentrixException {
        if (desde == null || hasta == null) {
            throw new DentrixException("El periodo del reporte mensual es obligatorio (desde y hasta)");
        }
        if (desde.isAfter(hasta)) {
            throw new DentrixException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        return reporteDAO.kpisDelMes(desde, hasta);
    }

    public List<ProductividadOdontologo> productividadOdontologos(LocalDate desde, LocalDate hasta)
            throws DentrixException {
        if (desde == null || hasta == null) {
            throw new DentrixException("El periodo del reporte es obligatorio (desde y hasta)");
        }
        return reporteDAO.productividadOdontologos(desde, hasta);
    }

    public List<SaldoPaciente> saldosPacientes() throws DentrixException {
        return reporteDAO.saldosPacientes();
    }

    public List<RankingTratamiento> rankingTratamientos() throws DentrixException {
        return reporteDAO.rankingTratamientos();
    }

    public List<Pago> documentosConSaldo() throws DentrixException {
        return pagoDAO.listarDocumentosConSaldo();
    }

    /**
     * Exporta los reportes del periodo consolidado a un libro Excel (.xlsx) usando Apache POI.
     * Incorpora formato institucional, estilos en encabezados y fórmulas/datos numéricos limpios.
     */
    public void exportarReporteExcel(LocalDate desde, LocalDate hasta, File destino) throws DentrixException {
        Preconditions.checkNotNull(desde, "La fecha 'desde' es obligatoria para exportar");
        Preconditions.checkNotNull(hasta, "La fecha 'hasta' es obligatoria para exportar");
        Preconditions.checkNotNull(destino, "El archivo de destino no puede ser nulo");
        Preconditions.checkArgument(!desde.isAfter(hasta), "La fecha 'desde' no puede ser posterior a 'hasta'");

        log.info("Generando reporte Excel profesional con Apache POI para el periodo {} a {}", desde, hasta);

        KpiMes kpi = kpisDelMes(desde, hasta);
        List<ProductividadOdontologo> productividad = productividadOdontologos(desde, hasta);
        List<SaldoPaciente> saldos = saldosPacientes();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Estilos
            XSSFFont fuenteTitulo = workbook.createFont();
            fuenteTitulo.setFontName("Segoe UI");
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            fuenteTitulo.setColor(IndexedColors.DARK_BLUE.getIndex());

            XSSFCellStyle estiloTitulo = workbook.createCellStyle();
            estiloTitulo.setFont(fuenteTitulo);

            XSSFFont fuenteCabecera = workbook.createFont();
            fuenteCabecera.setFontName("Segoe UI");
            fuenteCabecera.setBold(true);
            fuenteCabecera.setColor(IndexedColors.WHITE.getIndex());

            XSSFCellStyle estiloCabecera = workbook.createCellStyle();
            estiloCabecera.setFont(fuenteCabecera);
            estiloCabecera.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloCabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloCabecera.setBorderBottom(BorderStyle.THIN);
            estiloCabecera.setBorderTop(BorderStyle.THIN);
            estiloCabecera.setBorderLeft(BorderStyle.THIN);
            estiloCabecera.setBorderRight(BorderStyle.THIN);
            estiloCabecera.setAlignment(HorizontalAlignment.CENTER);

            XSSFCellStyle estiloBorde = workbook.createCellStyle();
            estiloBorde.setBorderBottom(BorderStyle.THIN);
            estiloBorde.setBorderTop(BorderStyle.THIN);
            estiloBorde.setBorderLeft(BorderStyle.THIN);
            estiloBorde.setBorderRight(BorderStyle.THIN);

            // Hoja 1: Indicadores y Productividad
            XSSFSheet hojaResumen = workbook.createSheet("Resumen e Indicadores");

            int r = 0;
            XSSFRow filaTit = hojaResumen.createRow(r++);
            XSSFCell celdaTit = filaTit.createCell(0);
            celdaTit.setCellValue("DENTRIX CLINIC - REPORTE DE GESTIÓN PERIODICA (" + desde + " al " + hasta + ")");
            celdaTit.setCellStyle(estiloTitulo);
            r++; // espacio

            // Tabla KPIs
            XSSFRow fKpiCab = hojaResumen.createRow(r++);
            String[] colKpis = {"Indicador de Gestión", "Valor"};
            for (int i = 0; i < colKpis.length; i++) {
                XSSFCell c = fKpiCab.createCell(i);
                c.setCellValue(colKpis[i]);
                c.setCellStyle(estiloCabecera);
            }

            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Recaudación Total del Periodo", "S/ " + kpi.recaudado().toPlainString());
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Ingresos en Efectivo", "S/ " + kpi.efectivo().toPlainString());
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Ingresos por Yape", "S/ " + kpi.yape().toPlainString());
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Ingresos por Plin", "S/ " + kpi.plin().toPlainString());
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Ingresos por Tarjeta", "S/ " + kpi.tarjeta().toPlainString());
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Total Citas Programadas", String.valueOf(kpi.citas()));
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Citas Efectivamente Atendidas", String.valueOf(kpi.atendidas()));
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Citas Inasistidas (Faltas)", String.valueOf(kpi.faltas()));
            agregarFilaKpi(hojaResumen, r++, estiloBorde, "Tasa de Inasistencia (No-Show)", kpi.noShowPct().toPlainString() + "%");

            r++; // espacio
            XSSFRow fProdCab = hojaResumen.createRow(r++);
            String[] colProd = {"Odontólogo / Profesional", "Atendidas", "Faltas", "Recaudado (S/)"};
            for (int i = 0; i < colProd.length; i++) {
                XSSFCell c = fProdCab.createCell(i);
                c.setCellValue(colProd[i]);
                c.setCellStyle(estiloCabecera);
            }

            for (ProductividadOdontologo prod : productividad) {
                XSSFRow fp = hojaResumen.createRow(r++);
                crearCelda(fp, 0, StringUtils.defaultString(prod.nombre(), "Sin asignar"), estiloBorde);
                crearCelda(fp, 1, String.valueOf(prod.atendidas()), estiloBorde);
                crearCelda(fp, 2, String.valueOf(prod.faltas()), estiloBorde);
                crearCelda(fp, 3, "S/ " + prod.recaudado().toPlainString(), estiloBorde);
            }

            hojaResumen.autoSizeColumn(0);
            hojaResumen.autoSizeColumn(1);
            hojaResumen.autoSizeColumn(2);
            hojaResumen.autoSizeColumn(3);

            // Hoja 2: Saldos de Pacientes
            XSSFSheet hojaSaldos = workbook.createSheet("Saldos Pendientes");
            int rs = 0;
            XSSFRow fSalTit = hojaSaldos.createRow(rs++);
            XSSFCell cSalTit = fSalTit.createCell(0);
            cSalTit.setCellValue("CARTERA DE SALDOS PENDIENTES POR COBRAR");
            cSalTit.setCellStyle(estiloTitulo);
            rs++;

            XSSFRow fSalCab = hojaSaldos.createRow(rs++);
            String[] colSal = {"Historia Clínica", "Paciente", "Saldo Pendiente", "N° Comprobantes"};
            for (int i = 0; i < colSal.length; i++) {
                XSSFCell c = fSalCab.createCell(i);
                c.setCellValue(colSal[i]);
                c.setCellStyle(estiloCabecera);
            }

            for (SaldoPaciente sp : saldos) {
                XSSFRow fs = hojaSaldos.createRow(rs++);
                crearCelda(fs, 0, StringUtils.trimToEmpty(sp.codigoHc()), estiloBorde);
                crearCelda(fs, 1, StringUtils.trimToEmpty(sp.nombre()), estiloBorde);
                crearCelda(fs, 2, "S/ " + sp.saldo().toPlainString(), estiloBorde);
                crearCelda(fs, 3, String.valueOf(sp.documentos()), estiloBorde);
            }

            for (int i = 0; i < 4; i++) {
                hojaSaldos.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(destino)) {
                workbook.write(fos);
            }

            log.info("Reporte Excel generado exitosamente en: {}", destino.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error al exportar reporte Excel a {}", destino.getAbsolutePath(), e);
            throw new DentrixException("No se pudo escribir el archivo Excel: " + e.getMessage(), e);
        }
    }

    private void agregarFilaKpi(XSSFSheet hoja, int r, XSSFCellStyle estilo, String indicador, String valor) {
        XSSFRow fila = hoja.createRow(r);
        crearCelda(fila, 0, indicador, estilo);
        crearCelda(fila, 1, valor, estilo);
    }

    private void crearCelda(XSSFRow fila, int col, String valor, XSSFCellStyle estilo) {
        XSSFCell celda = fila.createCell(col);
        celda.setCellValue(valor);
        celda.setCellStyle(estilo);
    }
}
