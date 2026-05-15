package com.cls.projetoluacarmesim;

import java.io.IOException;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void jogar() throws IOException {
        App.setRoot("streets");
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
