package com.possystem.sajilopos.controller.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.equals("admin") && password.equals("admin")) {

            try {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/dashboard/dashboard.fxml")
                );

                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid Credentials");
            alert.show();
        }
    }
}