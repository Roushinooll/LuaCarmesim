package com.cls.projetoluacarmesim;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static final double LARGURA_BASE = 1280;
    public static final double ALTURA_BASE = 720;

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        Parent root = loadFXML("menu");
        scene = new Scene(new StackPane(), LARGURA_BASE, ALTURA_BASE);
        aplicarRootResponsivo(root);

        stage.setScene(scene);
        stage.setTitle("Lua Carmesim");
        stage.show();
    }

    


    public static Object setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/cls/projetoluacarmesim/" + fxml + ".fxml")
        );

        Parent root = loader.load();
        aplicarRootResponsivo(root);

        return loader.getController();
    }

    



    private static void aplicarRootResponsivo(Parent root) {
        Group camadaJogo = new Group(root);
        StackPane wrapper = new StackPane(camadaJogo);
        wrapper.setStyle("-fx-background-color: black;");
        wrapper.setFocusTraversable(true);

        
        
        
        wrapper.getStylesheets().setAll(root.getStylesheets());
        if (!wrapper.getStyleClass().contains("root")) {
            wrapper.getStyleClass().add("root");
        }

        DoubleBinding escala = (DoubleBinding) Bindings.min(
                scene.widthProperty().divide(LARGURA_BASE),
                scene.heightProperty().divide(ALTURA_BASE)
        );

        camadaJogo.scaleXProperty().bind(escala);
        camadaJogo.scaleYProperty().bind(escala);

        scene.setRoot(wrapper);
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static Scene getScene() {
        return scene;
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
