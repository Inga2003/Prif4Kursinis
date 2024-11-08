module com.kursinis.prif4kursinis {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires spring.web;
    requires spring.core;
    requires spring.context;
    requires mysql.connector.j;
    requires com.google.gson;
    requires jbcrypt;

    opens com.kursinis.prif4kursinis to javafx.fxml;
    exports com.kursinis.prif4kursinis;
    opens com.kursinis.prif4kursinis.model to javafx.fxml, org.hibernate.orm.core;
    exports com.kursinis.prif4kursinis.model;
    opens com.kursinis.prif4kursinis.fxControllers to javafx.fxml;
    exports com.kursinis.prif4kursinis.fxControllers to javafx.fxml;
}