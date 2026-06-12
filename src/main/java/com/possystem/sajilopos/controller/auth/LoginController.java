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
 * LoginController - Handles company-based authentication
 * Requires: Company Code, Username, Password
 */
public class LoginController {

    @FXML
    private TextField companyCodeField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final AuthService authService = new AuthService();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void handleLogin() {
        String companyCode = companyCodeField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate input
        if (companyCode == null || companyCode.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            showError("Validation Error", "All fields are required", 
                     "Please enter company code, username, and password.");
            return;
        }

        // Attempt login
        if (authService.login(companyCode, username, password)) {
            User user = sessionManager.getCurrentUser();
            
            try {
                // Role-based navigation
                String dashboardPath = getDashboardPath(user.getRole());
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard - " + user.getRole());

            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Failed to load dashboard", e.getMessage());
            }

        } else {
            showError("Login Failed", "Invalid Credentials", 
                     "Company code, username, or password is incorrect.");
        }
    }

    /**
     * Get dashboard path based on user role
     */
    private String getDashboardPath(String role) {
        // All roles use the same dashboard for now
        // You can customize this for different role-based dashboards
        return "/fxml/dashboard/dashboard.fxml";
    }

    /**
     * Show error alert
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
