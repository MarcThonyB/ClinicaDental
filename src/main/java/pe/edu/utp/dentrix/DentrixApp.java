package pe.edu.utp.dentrix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.utp.dentrix.config.ConexionBD;

import java.io.IOException;

public class DentrixApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(DentrixApp.class);

    private static final String FXML_LOGIN = "/pe/edu/utp/dentrix/fxml/login.fxml";
    private static final String CSS_BASE = "/pe/edu/utp/dentrix/estilos.css";
    private static final int ANCHO = 1280;
    private static final int ALTO = 800;

    @Override
    public void start(Stage stage) throws IOException {
        log.info("Iniciando aplicación Dentrix Dental UI (JavaFX 25)...");
        FXMLLoader loader = new FXMLLoader(DentrixApp.class.getResource(FXML_LOGIN));
        Scene scene = new Scene(loader.load(), ANCHO, ALTO);
        scene.getStylesheets().add(DentrixApp.class.getResource(CSS_BASE).toExternalForm());
        stage.setTitle("Dentrix - Sistema de Gestion Clinica");
        stage.setScene(scene);
        stage.show();
        log.info("Ventana de Login desplegada correctamente.");
    }

    @Override
    public void stop() {
        log.info("Cerrando aplicación Dentrix Dental. Liberando recursos...");
        ConexionBD.cerrar();
        log.info("Aplicación cerrada limpiamente.");
    }

    public static void main(String[] args) {
        launch();
    }
}
