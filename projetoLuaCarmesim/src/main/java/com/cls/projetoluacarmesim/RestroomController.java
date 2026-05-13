package com.cls.projetoluacarmesim;

import java.io.IOException;
import javafx.fxml.FXML;

public class RestroomController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
