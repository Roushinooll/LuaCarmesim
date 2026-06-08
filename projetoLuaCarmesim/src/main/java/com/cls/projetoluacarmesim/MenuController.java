package com.cls.projetoluacarmesim;

import java.io.IOException;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void jogar() throws IOException {

        Object controller = App.setRoot("restroom");

        if (controller instanceof RestroomController) {
            RestroomController restroomController = (RestroomController) controller;
            restroomController.startGame(App.getStage().getScene());
        }
    }
    
    @FXML
    private void opcoes() throws IOException {
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