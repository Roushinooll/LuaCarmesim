package com.cls.projetoluacarmesim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Carrega o FXML inicial
        Parent root = loadFXML("menu");
        scene = new Scene(root, 1280, 720);

        stage.setScene(scene);
        stage.setTitle("Lua Carmesim");
        stage.show();
    }

    /**
     * Troca a raiz da scene atual e retorna o controller do FXML carregado.
     */
    public static Object setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/com/cls/projetoluacarmesim/" + fxml + ".fxml")
        );

        Parent root = loader.load();
        scene.setRoot(root);

        return loader.getController(); // Retorna o controller da nova tela
    }

    public static Stage getStage() {
        return primaryStage;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            App.class.getResource("/com/cls/projetoluacarmesim/" + fxml + ".fxml")
        );
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}