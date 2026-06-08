module com.cls.projetoluacarmesim {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires javafx.media;

    opens com.cls.projetoluacarmesim to javafx.fxml;
    exports com.cls.projetoluacarmesim;
}
