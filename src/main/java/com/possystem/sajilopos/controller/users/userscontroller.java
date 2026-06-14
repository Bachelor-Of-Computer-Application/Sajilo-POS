package com.possystem.sajilopos.controller.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class userscontroller {

    @FXML private TextField searchField;
    @FXML private TableView<User> usersTable;

    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());
        colRole.setCellValueFactory(data -> data.getValue().roleProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        loadMockData();
        usersTable.setItems(userList);
    }

    private void loadMockData() {
        userList.addAll(
                new User("U001", "Admin User", "admin@pos.com", "ADMIN", "ACTIVE"),
                new User("U002", "John Doe", "john@gmail.com", "CASHIER", "ACTIVE"),
                new User("U003", "Sara Sharma", "sara@gmail.com", "MANAGER", "INACTIVE"),
                new User("U004", "Mike Rai", "mike@gmail.com", "CASHIER", "ACTIVE")
        );
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().toLowerCase();

        if (keyword.isEmpty()) {
            usersTable.setItems(userList);
            return;
        }

        ObservableList<User> filtered = FXCollections.observableArrayList(
                userList.stream()
                        .filter(u ->
                                u.getName().toLowerCase().contains(keyword) ||
                                        u.getEmail().toLowerCase().contains(keyword) ||
                                        u.getRole().toLowerCase().contains(keyword)
                        )
                        .collect(Collectors.toList())
        );

        usersTable.setItems(filtered);
    }

    @FXML
    private void onAdd() {
        userList.add(new User("U999", "New User", "new@pos.com", "CASHIER", "ACTIVE"));
    }

    @FXML
    private void onEdit() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(selected.getName() + " (Edited)");
            usersTable.refresh();
        }
    }

    @FXML
    private void onDelete() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userList.remove(selected);
        }
    }

    public static class User {

        private final javafx.beans.property.SimpleStringProperty id;
        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleStringProperty email;
        private final javafx.beans.property.SimpleStringProperty role;
        private final javafx.beans.property.SimpleStringProperty status;

        public User(String id, String name, String email, String role, String status) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.role = new javafx.beans.property.SimpleStringProperty(role);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public javafx.beans.property.SimpleStringProperty idProperty() { return id; }
        public javafx.beans.property.SimpleStringProperty nameProperty() { return name; }
        public javafx.beans.property.SimpleStringProperty emailProperty() { return email; }
        public javafx.beans.property.SimpleStringProperty roleProperty() { return role; }
        public javafx.beans.property.SimpleStringProperty statusProperty() { return status; }

        public String getName() { return name.get(); }
        public String getEmail() { return email.get(); }
        public String getRole() { return role.get(); }

        public void setName(String value) { name.set(value); }
    }
}