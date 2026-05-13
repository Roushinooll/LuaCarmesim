module com.cls.projetoluacarmesim {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.cls.projetoluacarmesim to javafx.fxml;
    exports com.cls.projetoluacarmesim;
}
