package com.possystem.sajilopos.controller.users;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class userscontroller {

    @FXML private TextField searchField;
    @FXML private TableView<UserRow> usersTable;

    @FXML private TableColumn<UserRow, String> colId;
    @FXML private TableColumn<UserRow, String> colName;
    @FXML private TableColumn<UserRow, String> colRole;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colRole.setCellValueFactory(data -> data.getValue().roleProperty());

        loadUsers();
        usersTable.setItems(userList);
    }

    private void loadUsers() {
        try {
            List<com.possystem.sajilopos.model.User> users = userService.getAllUsers();
            userList.clear();
            for (com.possystem.sajilopos.model.User u : users) {
                userList.add(new UserRow(
                        String.valueOf(u.getUserId()),
                        u.getUsername(),
                        u.getRole()
                ));
            }
        } catch (IllegalStateException e) {
            showError("Access denied: " + e.getMessage());
        } catch (Exception e) {
            showError("Failed to load users: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            usersTable.setItems(userList);
            return;
        }
        ObservableList<UserRow> filtered = FXCollections.observableArrayList(
                userList.stream()
                        .filter(u -> u.getName().toLowerCase().contains(keyword)
                                || u.getRole().toLowerCase().contains(keyword))
                        .collect(Collectors.toList())
        );
        usersTable.setItems(filtered);
    }

    @FXML
    private void onAdd() {
        // SECURITY: Only ADMIN can create users
        SessionManager session = SessionManager.getInstance();
        if (!session.isAdmin()) {
            showError("Access Denied! Only administrators can create users.");
            return;
        }
        
        // Get username
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Create User");
        usernameDialog.setHeaderText("Enter username:");
        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (usernameResult.isEmpty()) return;

        // Get password
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Create User");
        passwordDialog.setHeaderText("Enter password (min 6 chars):");
        Optional<String> passwordResult = passwordDialog.showAndWait();
        if (passwordResult.isEmpty()) return;

        if (passwordResult.get().length() < 6) {
            showError("Password must be at least 6 characters long.");
            return;
        }

        // Get role
        ChoiceDialog<String> roleDialog = new ChoiceDialog<>("CASHIER", "CASHIER", "MANAGER", "ADMIN");
        roleDialog.setTitle("Create User");
        roleDialog.setHeaderText("Select role:");
        Optional<String> roleResult = roleDialog.showAndWait();
        if (roleResult.isEmpty()) return;

        try {
            boolean success = userService.createUser(
                    usernameResult.get(),
                    passwordResult.get(),
                    roleResult.get()
            );
            if (success) {
                showInfo("User created successfully.");
                loadUsers();
            } else {
                showError("Failed to create user.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        // SECURITY: Only ADMIN can edit users
        SessionManager session = SessionManager.getInstance();
        if (!session.isAdmin()) {
            showError("Access Denied! Only administrators can edit users.");
            return;
        }

        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a user to edit.");
            return;
        }

        // ── Username field ──────────────────────────────────────────
        TextInputDialog usernameDialog = new TextInputDialog(selected.getName());
        usernameDialog.setTitle("Edit User");
        usernameDialog.setHeaderText("Edit user: " + selected.getName());
        usernameDialog.setContentText("New username (leave unchanged to keep current):");
        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (usernameResult.isEmpty()) return;
        String newUsername = usernameResult.get().trim();
        if (newUsername.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }

        // ── Password field ──────────────────────────────────────────
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Edit User");
        passwordDialog.setHeaderText("Change password for: " + newUsername);
        passwordDialog.setContentText("New password (leave blank to keep current):");
        Optional<String> passwordResult = passwordDialog.showAndWait();
        if (passwordResult.isEmpty()) return;
        String newPassword = passwordResult.get(); // may be blank

        // ── Role field ──────────────────────────────────────────────
        ChoiceDialog<String> roleDialog = new ChoiceDialog<>(selected.getRole(), "CASHIER", "MANAGER", "ADMIN");
        roleDialog.setTitle("Edit User");
        roleDialog.setHeaderText("Select role for: " + newUsername);
        Optional<String> roleResult = roleDialog.showAndWait();
        if (roleResult.isEmpty()) return;

        try {
            int userId = Integer.parseInt(selected.getId());

            // Always update username + role
            boolean ok = userService.updateUser(userId, newUsername, roleResult.get());

            // If a new password was supplied, change it too
            if (ok && !newPassword.isEmpty()) {
                if (newPassword.length() < 6) {
                    showError("Password must be at least 6 characters. Role/username were updated.");
                } else {
                    // Use UserDAO directly via service to change password
                    // We need to call changePassword which verifies current — use admin override instead
                    userService.adminResetPassword(userId, newPassword);
                }
            }

            if (ok) {
                showInfo("User updated successfully.");
                loadUsers();
            } else {
                showError("Failed to update user.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        // SECURITY: Only ADMIN can delete users
        SessionManager session = SessionManager.getInstance();
        if (!session.isAdmin()) {
            showError("Access Denied! Only administrators can delete users.");
            return;
        }
        
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a user to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete user: " + selected.getName() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        try {
            boolean success = userService.deleteUser(Integer.parseInt(selected.getId()));
            if (success) {
                showInfo("User deleted.");
                loadUsers();
            } else {
                showError("Failed to delete user.");
            }
        } catch (IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for table rows
    public static class UserRow {
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty role;

        public UserRow(String id, String name, String role) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.role = new SimpleStringProperty(role);
        }

        public SimpleStringProperty idProperty() { return id; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty roleProperty() { return role; }

        public String getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getRole() { return role.get(); }
    }
}
