package com.possystem.sajilopos.controller.settings;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField newUsernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private ChoiceBox<String> themeChoice;
    @FXML private CheckBox taxCheck;
    @FXML private CheckBox discountCheck;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        themeChoice.getItems().addAll("Light", "Dark", "System Default");
        themeChoice.setValue("Light");

        roleChoice.getItems().addAll("ADMIN", "MANAGER", "CASHIER");
        roleChoice.setValue("CASHIER");
    }

    @FXML
    private void createUser(ActionEvent event) {
        // SECURITY: Only ADMIN can create users
        SessionManager session = SessionManager.getInstance();
        if (!session.isAdmin()) {
            showError("Access Denied!", "Only administrators can create users.");
            return;
        }
        
        String role = roleChoice.getValue();
        String username = newUsernameField.getText().trim();
        String password = newPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Username and password cannot be empty.", true);
            return;
        }

        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters long.", true);
            return;
        }

        try {
            boolean success = userService.createUser(username, password, role);
            if (success) {
                showStatus("User '" + username + "' created successfully as " + role, false);
                newUsernameField.clear();
                newPasswordField.clear();
                roleChoice.setValue("CASHIER");
            } else {
                showStatus("Failed to create user.", true);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    @FXML
    private void updateAccount(ActionEvent event) {}

    @FXML
    private void saveSystemSettings(ActionEvent event) {}

    @FXML
    private void changePassword(ActionEvent event) {}

    @FXML
    private void enable2FA(ActionEvent event) {}

    @FXML
    private void backupDatabase(ActionEvent event) {}

    @FXML
    private void restoreDatabase(ActionEvent event) {}

    @FXML
    private void clearCache(ActionEvent event) {}

    @FXML
    private void resetSystem(ActionEvent event) {}

    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        } else {
            System.out.println(message);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
