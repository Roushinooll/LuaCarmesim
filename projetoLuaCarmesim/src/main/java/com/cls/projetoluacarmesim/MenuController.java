package com.cls.projetoluacarmesim;

import java.io.IOException;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void jogar() throws IOException {
        Object controller = App.setRoot("intro");

        if (controller instanceof IntroController) {
            IntroController introController = (IntroController) controller;
            introController.startIntro();
        }
    }

    @FXML
    private void opcoes() throws IOException {
        EstadoJogo.getInstance().setTelaAnteriorConfigs("menu");
        App.setRoot("configs");
    }

    @FXML
    private void ranking() throws IOException {
        App.setRoot("ranking");
    }

    @FXML
    private void creditos() throws IOException {
        App.setRoot("creditos");
    }
}
