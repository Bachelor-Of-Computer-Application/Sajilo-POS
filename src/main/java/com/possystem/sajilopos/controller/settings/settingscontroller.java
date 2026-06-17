package com.possystem.sajilopos.controller.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class settingscontroller {

    // Account Settings
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // User Management
    @FXML
    private TextField newUsernameField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private ChoiceBox<String> roleChoice;

    // System Settings
    @FXML
    private ChoiceBox<String> themeChoice;

    @FXML
    private CheckBox taxCheck;

    @FXML
    private CheckBox discountCheck;

    @FXML
    public void initialize() {

        // Theme Options
        themeChoice.getItems().addAll(
                "Light",
                "Dark",
                "System Default"
        );

        themeChoice.setValue("Light");

        // User Roles
        roleChoice.getItems().addAll(
                "Admin",
                "Manager",
                "Cashier"
        );

        roleChoice.setValue("Cashier");
    }

    @FXML
    private void updateAccount(ActionEvent event) {

    }
    @FXML
    private void createUser(ActionEvent event) {

        String role = roleChoice.getValue();
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();

        System.out.println("Role: " + role);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // INSERT INTO users(role, username, password)

        newUsernameField.clear();
        newPasswordField.clear();
        roleChoice.setValue("Cashier");
    }
    @FXML
    private void saveSystemSettings(ActionEvent event) {

    }

    @FXML
    private void changePassword(ActionEvent event) {

    }

    @FXML
    private void enable2FA(ActionEvent event) {

    }

    @FXML
    private void backupDatabase(ActionEvent event) {

    }

    @FXML
    private void restoreDatabase(ActionEvent event) {

    }

    @FXML
    private void clearCache(ActionEvent event) {

    }

    @FXML
    private void resetSystem(ActionEvent event) {

    }
}