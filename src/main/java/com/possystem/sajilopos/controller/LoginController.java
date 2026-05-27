package com.possystem.sajilopos.controller;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private AuthService authService = new AuthService();

    
    @FXML
    public void initialize() {
        errorLabel.setText("");

        if (!DBConnection.testConnection()) {
            errorLabel.setText("ERROR: Cannot connect to database. Check config.properties");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    
    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        if (authService.login(username, password)) {
            showSuccess("Login successful!");

            clearFields();
        } else {
            showError("Invalid username or password");
            passwordField.clear();
        }
    }

    
    @FXML
    protected void onClearButtonClick() {
        clearFields();
        errorLabel.setText("");
    }

 
    private void showError(String message) {
        errorLabel.setText("ERROR: " + message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
    }

   
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        usernameField.requestFocus();
    }
}
