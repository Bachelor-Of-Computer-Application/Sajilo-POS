package com.possystem.sajilopos.controller.settings;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.model.User;
import com.possystem.sajilopos.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class settingscontroller {

    // ── Section cards (shown/hidden by role) ─────────────────────────────────
    @FXML private VBox accountSection;
    @FXML private VBox changePasswordSection;
    @FXML private VBox createUserSection;
    @FXML private VBox updateRoleSection;
    @FXML private VBox systemSettingsSection;
    @FXML private VBox backupSection;
    @FXML private VBox systemActionsSection;

    // ── Header ───────────────────────────────────────────────────────────────
    @FXML private Label sessionInfoLabel;

    // ── Account Settings (all roles) ─────────────────────────────────────────
    @FXML private PasswordField currentPasswordAccountField;
    @FXML private TextField     newUsernameAccountField;
    @FXML private PasswordField newPasswordAccountField;

    // ── Change Password (all roles) ──────────────────────────────────────────
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;

    // ── Create User (Admin only) ──────────────────────────────────────────────
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private TextField         newUsernameField;
    @FXML private PasswordField     newUserPasswordField;

    // ── Update User Role (Admin only) ─────────────────────────────────────────
    @FXML private ComboBox<String>  userListCombo;
    @FXML private ChoiceBox<String> updateRoleChoice;

    // ── System Settings (Admin only) ─────────────────────────────────────────
    @FXML private ChoiceBox<String> themeChoice;
    @FXML private CheckBox          taxCheck;
    @FXML private CheckBox          discountCheck;

    // ── Shared feedback ──────────────────────────────────────────────────────
    @FXML private Label statusLabel;

    private final UserService    userService = new UserService();
    private final SessionManager session     = SessionManager.getInstance();

    private List<User> companyUsers;

    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Populate dropdowns
        themeChoice.getItems().addAll("Light", "Dark", "System Default");
        themeChoice.setValue("Light");

        roleChoice.getItems().addAll("ADMIN", "MANAGER", "CASHIER");
        roleChoice.setValue("CASHIER");

        updateRoleChoice.getItems().addAll("ADMIN", "MANAGER", "CASHIER");
        updateRoleChoice.setValue("CASHIER");

        // Show current user info in the header badge
        String name = session.getCurrentUserName();
        String role = session.getCurrentUserRole();
        if (name != null) {
            sessionInfoLabel.setText("Logged in as: " + name + " (" + role + ")");
            newUsernameAccountField.setText(name);  // pre-fill own username
        }

        // Show / hide sections based on role
        applyRoleVisibility();

        // Populate user list for admin role-update section
        loadUserList();
    }

    /**
     * Show admin-only sections only when the current user is ADMIN.
     * MANAGER and CASHIER only see Account Settings and Change Password.
     */
    private void applyRoleVisibility() {
        boolean isAdmin = session.isAdmin();

        setVisible(createUserSection,     isAdmin);
        setVisible(updateRoleSection,     isAdmin);
        setVisible(systemSettingsSection, isAdmin);
        setVisible(backupSection,         isAdmin);
        setVisible(systemActionsSection,  isAdmin);
    }

    /** Hides the node AND collapses its space so the layout doesn't leave a gap. */
    private void setVisible(VBox node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);  // removes it from layout flow when hidden
    }

    // ── Account Settings ─────────────────────────────────────────────────────

    @FXML
    private void updateAccount(ActionEvent event) {
        String currentPw   = currentPasswordAccountField.getText();
        String newUsername = newUsernameAccountField.getText().trim();
        String newPw       = newPasswordAccountField.getText();

        if (currentPw.isEmpty() || newUsername.isEmpty() || newPw.isEmpty()) {
            showStatus("All fields are required to update your account.", true);
            return;
        }

        try {
            boolean ok = userService.updateOwnAccount(currentPw, newUsername, newPw);
            if (ok) {
                showStatus("Account updated. Username is now '" + newUsername + "'.", false);
                currentPasswordAccountField.clear();
                newPasswordAccountField.clear();
                newUsernameAccountField.setText(newUsername);
                // Refresh header badge
                sessionInfoLabel.setText("Logged in as: " + newUsername
                        + " (" + session.getCurrentUserRole() + ")");
            } else {
                showStatus("Update failed. Please try again.", true);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    // ── Change Password ───────────────────────────────────────────────────────

    @FXML
    private void changePassword(ActionEvent event) {
        String currentPw = currentPasswordField.getText();
        String newPw     = newPasswordField.getText();

        if (currentPw.isEmpty() || newPw.isEmpty()) {
            showStatus("Both password fields are required.", true);
            return;
        }

        try {
            boolean ok = userService.changePassword(currentPw, newPw);
            if (ok) {
                showStatus("Password changed successfully.", false);
                currentPasswordField.clear();
                newPasswordField.clear();
            } else {
                showStatus("Password change failed. Please try again.", true);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    // ── Create User (Admin) ───────────────────────────────────────────────────

    @FXML
    private void createUser(ActionEvent event) {
        String username = newUsernameField.getText().trim();
        String password = newUserPasswordField.getText();
        String role     = roleChoice.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Username and password cannot be empty.", true);
            return;
        }

        try {
            boolean ok = userService.createUser(username, password, role);
            if (ok) {
                showStatus("User '" + username + "' created as " + role + ".", false);
                newUsernameField.clear();
                newUserPasswordField.clear();
                roleChoice.setValue("CASHIER");
                loadUserList();
            } else {
                showStatus("Failed to create user.", true);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    // ── Update User Role (Admin) ──────────────────────────────────────────────

    @FXML
    private void updateUserRole(ActionEvent event) {
        String selectedDisplay = userListCombo.getValue();
        String newRole         = updateRoleChoice.getValue();

        if (selectedDisplay == null || selectedDisplay.isEmpty()) {
            showStatus("Please select a user first.", true);
            return;
        }

        User target = findUserByDisplay(selectedDisplay);
        if (target == null) {
            showStatus("Selected user not found.", true);
            return;
        }

        try {
            boolean ok = userService.updateUserRole(target.getUserId(), newRole);
            if (ok) {
                showStatus("Role of '" + target.getUsername() + "' updated to " + newRole + ".", false);
                loadUserList();
            } else {
                showStatus("Failed to update role.", true);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    // ── System Settings (Admin) ───────────────────────────────────────────────

    @FXML
    private void saveSystemSettings(ActionEvent event) {
        String  theme           = themeChoice.getValue();
        boolean taxEnabled      = taxCheck.isSelected();
        boolean discountEnabled = discountCheck.isSelected();

        // TODO: persist to a settings DB table
        System.out.println("System settings: theme=" + theme
                + " tax=" + taxEnabled + " discount=" + discountEnabled);
        showStatus("System settings saved (theme: " + theme + ").", false);
    }

    // ── Stubs (Admin, not yet fully implemented) ──────────────────────────────

    @FXML
    private void enable2FA(ActionEvent event) {
        showStatus("2FA is not yet implemented.", true);
    }

    @FXML
    private void backupDatabase(ActionEvent event) {
        showStatus("Database backup is not yet implemented.", true);
    }

    @FXML
    private void restoreDatabase(ActionEvent event) {
        showStatus("Database restore is not yet implemented.", true);
    }

    @FXML
    private void clearCache(ActionEvent event) {
        showStatus("Cache cleared.", false);
    }

    @FXML
    private void resetSystem(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Reset");
        confirm.setHeaderText("Reset System");
        confirm.setContentText("Are you sure? This cannot be undone.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn.getText().equals("OK")) {
                showStatus("System reset is not yet implemented.", true);
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void loadUserList() {
        if (userListCombo == null || !session.isAdmin()) return;

        userListCombo.getItems().clear();
        try {
            companyUsers = userService.getAllUsers();
            for (User u : companyUsers) {
                userListCombo.getItems().add(displayName(u));
            }
        } catch (Exception e) {
            System.err.println("Could not load user list: " + e.getMessage());
        }
    }

    private String displayName(User u) {
        return u.getUsername() + " (" + u.getRole() + ")";
    }

    private User findUserByDisplay(String display) {
        if (companyUsers == null) return null;
        for (User u : companyUsers) {
            if (displayName(u).equals(display)) return u;
        }
        return null;
    }

    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle(isError
                    ? "-fx-text-fill: #ef4444;"
                    : "-fx-text-fill: #10b981;");
        }
    }
}
