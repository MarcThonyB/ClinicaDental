package pe.edu.utp.dentrix.util;

import pe.edu.utp.dentrix.config.ConexionBD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public final class TestDatabaseHelper {

    private TestDatabaseHelper() {
    }

    public static synchronized void asegurarSemilla() {
        try (Connection cn = ConexionBD.abrir()) {
            if (cn == null) {
                return;
            }
            if (!necesitaRestauracion(cn)) {
                return;
            }
            restaurarSemilla(cn);
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo asegurar la semilla de pruebas: " + e.getMessage());
        }
    }

    private static boolean necesitaRestauracion(Connection cn) {
        try (Statement st = cn.createStatement()) {
            // Verificar KPI M2: exactamente 2 pacientes con saldo deudor que suman 650
            try (ResultSet rs = st.executeQuery(
                    "SELECT count(*), COALESCE(SUM(saldo_total), 0) FROM dentrix.v_saldos_paciente WHERE saldo_total > 0")) {
                if (!rs.next() || rs.getLong(1) != 2 || rs.getBigDecimal(2).compareTo(new java.math.BigDecimal("650.00")) != 0) {
                    return true;
                }
            }
            // Verificar ranking lider TRAT-05
            try (ResultSet rs = st.executeQuery(
                    "SELECT codigo FROM dentrix.v_ranking_tratamientos LIMIT 1")) {
                if (!rs.next() || !"TRAT-05".equals(rs.getString(1))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static void restaurarSemilla(Connection cn) throws Exception {
        Path basePath = Path.of("sql");
        if (!Files.isDirectory(basePath)) {
            basePath = Path.of("../sql");
        }
        ejecutarArchivoSql(cn, basePath.resolve("01_schema_v2.sql"));
        try (Statement st = cn.createStatement()) {
            st.execute("SET search_path TO dentrix, public;");
        }
        ejecutarArchivoSql(cn, basePath.resolve("02_datos_prueba_v2.sql"));
        try (Statement st = cn.createStatement()) {
            st.execute("SET search_path TO dentrix, public;");
        }
        ejecutarArchivoSql(cn, basePath.resolve("03_optimizacion_rendimiento.sql"));
    }

    private static void ejecutarArchivoSql(Connection cn, Path archivo) throws IOException, java.sql.SQLException {
        if (!Files.isRegularFile(archivo)) {
            throw new IOException("Archivo SQL no encontrado: " + archivo.toAbsolutePath());
        }
        String sql = Files.readString(archivo, java.nio.charset.StandardCharsets.UTF_8);
        try (Statement st = cn.createStatement()) {
            st.execute(sql);
        }
    }
}
