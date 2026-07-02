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

import java.util.List;
import java.util.Optional;

public class settingscontroller {

    // Create User card
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private TextField newUsernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label createStatusLabel;

    // Manage Users card
    @FXML private TableView<UserRow> usersTable;
    @FXML private TableColumn<UserRow, String> colId;
    @FXML private TableColumn<UserRow, String> colName;
    @FXML private TableColumn<UserRow, String> colRole;
    @FXML private Label manageStatusLabel;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        roleChoice.getItems().addAll("ADMIN", "MANAGER", "CASHIER");
        roleChoice.setValue("CASHIER");

        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colRole.setCellValueFactory(data -> data.getValue().roleProperty());
        usersTable.setItems(userList);

        loadUsers();
    }

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

    @FXML
    private void createUser(ActionEvent event) {
        if (!SessionManager.getInstance().isAdmin()) {
            setStatus(createStatusLabel, "Access Denied! Admins only.", true);
            return;
        }
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
            boolean success = userService.createUser(username, password, role);
            if (success) {
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

    @FXML
    private void onEditUser(ActionEvent event) {
        if (!SessionManager.getInstance().isAdmin()) {
            setStatus(manageStatusLabel, "Access Denied! Admins only.", true);
            return;
        }
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus(manageStatusLabel, "Please select a user to edit.", true);
            return;
        }
        TextInputDialog usernameDialog = new TextInputDialog(selected.getName());
        usernameDialog.setTitle("Edit User");
        usernameDialog.setHeaderText("Edit username for: " + selected.getName());
        usernameDialog.setContentText("Username:");
        Optional<String> usernameResult = usernameDialog.showAndWait();
        if (usernameResult.isEmpty() || usernameResult.get().trim().isEmpty()) return;

        ChoiceDialog<String> roleDialog = new ChoiceDialog<>(selected.getRole(), "CASHIER", "MANAGER", "ADMIN");
        roleDialog.setTitle("Edit User");
        roleDialog.setHeaderText("Select new role for: " + selected.getName());
        Optional<String> roleResult = roleDialog.showAndWait();
        if (roleResult.isEmpty()) return;

        try {
            boolean success = userService.updateUser(
                    Integer.parseInt(selected.getId()),
                    usernameResult.get().trim(),
                    roleResult.get()
            );
            if (success) {
                setStatus(manageStatusLabel, "User updated successfully.", false);
                loadUsers();
            } else {
                setStatus(manageStatusLabel, "Failed to update user.", true);
            }
        } catch (Exception e) {
            setStatus(manageStatusLabel, e.getMessage(), true);
        }
    }

    @FXML
    private void onChangePassword(ActionEvent event) {
        if (!SessionManager.getInstance().isAdmin()) {
            setStatus(manageStatusLabel, "Access Denied! Admins only.", true);
            return;
        }
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus(manageStatusLabel, "Please select a user to change password.", true);
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Set new password for: " + selected.getName());
        dialog.setContentText("New password (min 6 chars):");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) return;
        if (result.get().length() < 6) {
            setStatus(manageStatusLabel, "Password must be at least 6 characters.", true);
            return;
        }
        // TODO: replace once UserDAO.changePassword() is added manually
        setStatus(manageStatusLabel, "Password change: DB method not yet added. Coming soon.", true);
    }

    @FXML
    private void onDeleteUser(ActionEvent event) {
        if (!SessionManager.getInstance().isAdmin()) {
            setStatus(manageStatusLabel, "Access Denied! Admins only.", true);
            return;
        }
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus(manageStatusLabel, "Please select a user to delete.", true);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete user: " + selected.getName() + "?");
        confirm.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        try {
            boolean success = userService.deleteUser(Integer.parseInt(selected.getId()));
            if (success) {
                setStatus(manageStatusLabel, "User deleted successfully.", false);
                loadUsers();
            } else {
                setStatus(manageStatusLabel, "Failed to delete user.", true);
            }
        } catch (Exception e) {
            setStatus(manageStatusLabel, e.getMessage(), true);
        }
    }

    private void setStatus(Label label, String message, boolean isError) {
        label.setText(message);
        label.setStyle(isError
                ? "-fx-text-fill: #dc2626; -fx-font-size: 12px;"
                : "-fx-text-fill: #16a34a; -fx-font-size: 12px;");
    }

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
