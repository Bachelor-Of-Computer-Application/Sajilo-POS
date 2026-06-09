module com.possystem.sajilopos {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.possystem.sajilopos to javafx.fxml;
    opens com.possystem.sajilopos.controller.auth to javafx.fxml;
    opens com.possystem.sajilopos.controller.dashboard to javafx.fxml;

    exports com.possystem.sajilopos;
}