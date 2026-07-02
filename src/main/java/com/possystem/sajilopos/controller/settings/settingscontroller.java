package com.possystem.sajilopos.controller.settings;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.model.User;
import com.possystem.sajilopos.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class settingscontroller {

    // ── Header ────────────────────────────────────────────────────────────
    @FXML private Label sessionInfoLabel;

    // ── Card visibility (role-based) ──────────────────────────────────────
    @FXML private VBox changePasswordCard;  // all roles
    @FXML private VBox createUserCard;      // admin only
    @FXML private VBox manageUsersCard;     // admin only

    // ── Change Own Password (all roles) ───────────────────────────────────
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newOwnPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         passwordStatusLabel;

    // ── Create User (admin only) ──────────────────────────────────────────
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private TextField         newUsernameField;
    @FXML private PasswordField     newPasswordField;
    @FXML private Label             createStatusLabel;

    // ── Manage Users table (admin only) ───────────────────────────────────
    @FXML private TableView<UserRow>         usersTable;
    @FXML private TableColumn<UserRow, String> colId;
    @FXML private TableColumn<UserRow, String> colName;
    @FXML private TableColumn<UserRow, String> colRole;
    @FXML private Label                      manageStatusLabel;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();
    private final UserService    userService = new UserService();
    private final SessionManager session     = SessionManager.getInstance();

    // ─────────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Session badge
        User current = session.getCurrentUser();
        if (current != null) {
            sessionInfoLabel.setText(current.getUsername() + "  (" + current.getRole() + ")");
        }

        // Role choices for new user
        roleChoice.getItems().addAll("ADMIN", "MANAGER", "CASHIER");
        roleChoice.setValue("CASHIER");

        // Table bindings
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colRole.setCellValueFactory(data -> data.getValue().roleProperty());
        usersTable.setItems(userList);

        // Show/hide admin cards based on role
        applyRoleVisibility();

        // Load user list (only meaningful for admin)
        if (session.isAdmin()) {
            loadUsers();
        }
    }

    // ── Role-based visibility ─────────────────────────────────────────────

    private void applyRoleVisibility() {
        boolean isAdmin = session.isAdmin();
        setCard(createUserCard,  isAdmin);
        setCard(manageUsersCard, isAdmin);
        // changePasswordCard is always visible
    }

    private void setCard(VBox card, boolean visible) {
        card.setVisible(visible);
        card.setManaged(visible);
    }

    // ── Change Own Password (all roles) ──────────────────────────────────

    @FXML
    private void changeOwnPassword(ActionEvent event) {
        String current  = currentPasswordField.getText();
        String newPw    = newOwnPasswordField.getText();
        String confirm  = confirmPasswordField.getText();

        if (current.isEmpty() || newPw.isEmpty() || confirm.isEmpty()) {
            setStatus(passwordStatusLabel, "All password fields are required.", true);
            return;
        }
        if (!newPw.equals(confirm)) {
            setStatus(passwordStatusLabel, "New passwords do not match.", true);
            return;
        }
        if (newPw.length() < 6) {
            setStatus(passwordStatusLabel, "New password must be at least 6 characters.", true);
            return;
        }
        try {
            boolean ok = userService.changePassword(current, newPw);
            if (ok) {
                setStatus(passwordStatusLabel, "Password changed successfully.", false);
                currentPasswordField.clear();
                newOwnPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                setStatus(passwordStatusLabel, "Failed to change password. Try again.", true);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            setStatus(passwordStatusLabel, e.getMessage(), true);
        }
    }

    // ── Create User (admin) ───────────────────────────────────────────────

    @FXML
    private void createUser(ActionEvent event) {
        String role     = roleChoice.getValue();
        String username = newUsernameField.getText().trim();
        String password = newPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setStatus(createStatusLabel, "Username and password cannot be empty.", true);
            return;
        }
        if (password.length() < 6) {
            setStatus(createStatusLabel, "Password must be at least 6 characters.", true);
            return;
        }
        try {
            boolean ok = userService.createUser(username, password, role);
            if (ok) {
                setStatus(createStatusLabel, "User '" + username + "' created as " + role + ".", false);
                newUsernameField.clear();
                newPasswordField.clear();
                roleChoice.setValue("CASHIER");
                loadUsers();
            } else {
                setStatus(createStatusLabel, "Failed to create user.", true);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            setStatus(createStatusLabel, e.getMessage(), true);
        }
    }

    // ── Edit User (admin) ─────────────────────────────────────────────────

    @FXML
    private void onEditUser(ActionEvent event) {
        UserRow selected = getSelectedUser();
        if (selected == null) return;

        // Username dialog
        TextInputDialog usernameDialog = new TextInputDialog(selected.getName());
        usernameDialog.setTitle("Edit User");
        usernameDialog.setHeaderText("Edit username");
        usernameDialog.setContentText("Username:");
        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (usernameResult.isEmpty() || usernameResult.get().trim().isEmpty()) return;

        // Role dialog
        ChoiceDialog<String> roleDialog = new ChoiceDialog<>(selected.getRole(), "CASHIER", "MANAGER", "ADMIN");
        roleDialog.setTitle("Edit User");
        roleDialog.setHeaderText("Select new role for: " + selected.getName());
        Optional<String> roleResult = roleDialog.showAndWait();
        if (roleResult.isEmpty()) return;

        try {
            boolean ok = userService.updateUser(
                    Integer.parseInt(selected.getId()),
                    usernameResult.get().trim(),
                    roleResult.get()
            );
            if (ok) {
                setStatus(manageStatusLabel, "User updated successfully.", false);
                loadUsers();
            } else {
                setStatus(manageStatusLabel, "Failed to update user.", true);
            }
        } catch (Exception e) {
            setStatus(manageStatusLabel, e.getMessage(), true);
        }
    }

    // ── Reset Password (admin — no need for old password) ─────────────────

    @FXML
    private void onChangePassword(ActionEvent event) {
        UserRow selected = getSelectedUser();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password for: " + selected.getName());
        dialog.setContentText("New password (min 6 chars):");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) return;

        String newPw = result.get().trim();
        if (newPw.length() < 6) {
            setStatus(manageStatusLabel, "Password must be at least 6 characters.", true);
            return;
        }

        try {
            boolean ok = userService.adminResetPassword(Integer.parseInt(selected.getId()), newPw);
            if (ok) {
                setStatus(manageStatusLabel, "Password reset for '" + selected.getName() + "'.", false);
            } else {
                setStatus(manageStatusLabel, "Failed to reset password.", true);
            }
        } catch (Exception e) {
            setStatus(manageStatusLabel, e.getMessage(), true);
        }
    }

    // ── Delete User (admin) ───────────────────────────────────────────────

    @FXML
    private void onDeleteUser(ActionEvent event) {
        UserRow selected = getSelectedUser();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete user: " + selected.getName() + "?");
        confirm.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        try {
            boolean ok = userService.deleteUser(Integer.parseInt(selected.getId()));
            if (ok) {
                setStatus(manageStatusLabel, "User deleted successfully.", false);
                loadUsers();
            } else {
                setStatus(manageStatusLabel, "Failed to delete user.", true);
            }
        } catch (Exception e) {
            setStatus(manageStatusLabel, e.getMessage(), true);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            userList.clear();
            for (User u : users) {
                userList.add(new UserRow(
                        String.valueOf(u.getUserId()),
                        u.getUsername(),
                        u.getRole()
                ));
            }
        } catch (Exception e) {
            setStatus(manageStatusLabel, "Could not load users: " + e.getMessage(), true);
        }
    }

    /** Returns selected row, or shows an error and returns null. */
    private UserRow getSelectedUser() {
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus(manageStatusLabel, "Please select a user from the table first.", true);
        }
        return selected;
    }

    private void setStatus(Label label, String message, boolean isError) {
        label.setText(message);
        label.setStyle(isError
                ? "-fx-text-fill: #dc2626; -fx-font-size: 12px;"
                : "-fx-text-fill: #16a34a; -fx-font-size: 12px;");
    }

    // ── Inner class ───────────────────────────────────────────────────────

    public static class UserRow {
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty role;

        public UserRow(String id, String name, String role) {
            this.id   = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.role = new SimpleStringProperty(role);
        }

        public SimpleStringProperty idProperty()   { return id; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty roleProperty() { return role; }

        public String getId()   { return id.get(); }
        public String getName() { return name.get(); }
        public String getRole() { return role.get(); }
    }
}
