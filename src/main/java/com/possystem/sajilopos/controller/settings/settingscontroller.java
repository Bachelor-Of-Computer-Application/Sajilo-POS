package com.possystem.sajilopos.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class settingscontroller {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> themeChoice;
    @FXML private CheckBox taxCheck;
    @FXML private CheckBox discountCheck;

    // 🔥 initialize UI values
    @FXML
    public void initialize() {
        themeChoice.getItems().addAll("Light", "Dark", "System Default");
        themeChoice.setValue("Light");

        System.out.println("Settings loaded");
    }

    // ================= ACCOUNT =================
    @FXML
    private void updateAccount() {
        System.out.println("Updating account...");
        System.out.println("Username: " + usernameField.getText());
    }

    // ================= SYSTEM =================
    @FXML
    private void saveSystemSettings() {
        System.out.println("Saving system settings...");
        System.out.println("Theme: " + themeChoice.getValue());
        System.out.println("Tax: " + taxCheck.isSelected());
        System.out.println("Discount: " + discountCheck.isSelected());
    }

    // ================= SECURITY =================
    @FXML
    private void changePassword() {
        System.out.println("Change password clicked");
    }

    @FXML
    private void enable2FA() {
        System.out.println("2FA enabled (mock)");
    }

    // ================= BACKUP =================
    @FXML
    private void backupDatabase() {
        System.out.println("Database backup started...");
    }

    @FXML
    private void restoreDatabase() {
        System.out.println("Database restore started...");
    }

    // ================= SYSTEM ACTIONS =================
    @FXML
    private void clearCache() {
        System.out.println("Cache cleared");
    }

    @FXML
    private void resetSystem() {
        System.out.println("SYSTEM RESET WARNING");
    }
}