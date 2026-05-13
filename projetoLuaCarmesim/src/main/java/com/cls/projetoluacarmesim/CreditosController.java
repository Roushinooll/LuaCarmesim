package com.cls.projetoluacarmesim;

import java.io.IOException;
import javafx.fxml.FXML;

public class CreditosController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
