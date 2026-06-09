module com.possystem.sajilopos {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires jbcrypt;

    opens com.possystem.sajilopos to javafx.fxml;
    opens com.possystem.sajilopos.controller.auth to javafx.fxml;
    opens com.possystem.sajilopos.controller.dashboard to javafx.fxml;
    opens com.possystem.sajilopos.controller.product to javafx.fxml;

    exports com.possystem.sajilopos;
}