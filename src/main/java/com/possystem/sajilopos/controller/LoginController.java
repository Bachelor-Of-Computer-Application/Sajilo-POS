package com.possystem.sajilopos.controller;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Login Controller
 * Handles user authentication via the JavaFX UI
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private AuthService authService = new AuthService();

    /**
     * Initialize the login screen
     */
    @FXML
    public void initialize() {
        errorLabel.setText("");

        // Test database connection on startup
        if (!DBConnection.testConnection()) {
            errorLabel.setText("ERROR: Cannot connect to database. Check config.properties");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handle login button click
     */
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
            // TODO: Navigate to main POS screen
            // Example:
            // Stage stage = (Stage) usernameField.getScene().getWindow();
            // FXMLLoader loader = new
            // FXMLLoader(getClass().getResource("/fxml/main-screen.fxml"));
            // Scene scene = new Scene(loader.load());
            // stage.setScene(scene);

            clearFields();
        } else {
            showError("Invalid username or password");
            passwordField.clear();
        }
    }

    /**
     * Handle clear button click
     */
    @FXML
    protected void onClearButtonClick() {
        clearFields();
        errorLabel.setText("");
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        errorLabel.setText("ERROR: " + message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Display success message
     */
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
    }

    /**
     * Clear input fields
     */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        usernameField.requestFocus();
    }
}
