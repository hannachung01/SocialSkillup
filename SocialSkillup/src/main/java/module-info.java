module com.example.socialskillup1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mail;
    requires org.xerial.sqlitejdbc;


    opens com.example.socialskillup1 to javafx.fxml;
    exports com.example.socialskillup1;
}