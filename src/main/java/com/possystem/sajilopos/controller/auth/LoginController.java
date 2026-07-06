package com.possystem.sajilopos.controller.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.service.AuthService;
import com.possystem.sajilopos.model.User;

/**
 * LoginController - Handles username/password authentication (no company code).
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final AuthService authService = new AuthService();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Validation Error", "Missing fields", "Please enter your username and password.");
            return;
        }

        if (authService.login(username, password)) {
            User user = sessionManager.getCurrentUser();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard/dashboard.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard - " + user.getRole());
                stage.setMaximized(true);

            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Failed to load dashboard", e.getMessage());
            }
        } else {
            showError("Login Failed", "Invalid Credentials", "Username or password is incorrect.");
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
