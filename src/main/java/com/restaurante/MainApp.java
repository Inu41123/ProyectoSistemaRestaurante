package com.restaurante;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.restaurante.controllers.MainController;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Cargar primero el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurante/views/main.fxml"));
            Parent root = loader.load();

            // 2. Configurar la escena
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            primaryStage.setTitle("Sistema Restaurante - Delicias Gourmet");
            primaryStage.setScene(scene);

            // 3. Obtener el controlador principal
            MainController mainController = loader.getController();

            // 4. Maximizar y mostrar la ventana
            primaryStage.setMaximized(true);
            primaryStage.show();

            // 5. Inicialización tardía (después de mostrar la ventana)
            Platform.runLater(mainController::postInitialize);

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}