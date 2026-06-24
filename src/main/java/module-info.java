module com.possystem.sajilopos {

    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires jbcrypt;

    opens com.possystem.sajilopos to javafx.fxml;
    opens com.possystem.sajilopos.controller.auth to javafx.fxml;
    opens com.possystem.sajilopos.controller.dashboard to javafx.fxml;
    opens com.possystem.sajilopos.controller.product to javafx.fxml;
    opens com.possystem.sajilopos.controller.customers to javafx.fxml;
    opens com.possystem.sajilopos.controller.inventory to javafx.fxml;
    opens com.possystem.sajilopos.controller.purchases to javafx.fxml;
    opens com.possystem.sajilopos.controller.sales to javafx.fxml;
    opens com.possystem.sajilopos.controller.reports to javafx.fxml;
    opens com.possystem.sajilopos.controller.users to javafx.fxml;
    opens com.possystem.sajilopos.controller.settings to javafx.fxml;
    opens com.possystem.sajilopos.controller.suppliers to javafx.fxml;

    exports com.possystem.sajilopos;
}