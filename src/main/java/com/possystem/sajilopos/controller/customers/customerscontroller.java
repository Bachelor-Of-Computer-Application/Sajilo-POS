package com.possystem.sajilopos.controller.customers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class CustomerController {

    @FXML private TextField searchField;
    @FXML private TableView<Customer> customersTable;

    @FXML private TableColumn<Customer, String> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colAddress;
    @FXML private TableColumn<Customer, Integer> colLoyalty;

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colPhone.setCellValueFactory(data -> data.getValue().phoneProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());
        colAddress.setCellValueFactory(data -> data.getValue().addressProperty());
        colLoyalty.setCellValueFactory(data -> data.getValue().loyaltyProperty().asObject());

        loadMockData();
        customersTable.setItems(customerList);
    }

    private void loadMockData() {
        customerList.addAll(
                new Customer("C001", "Ram Sharma", "9800000001", "ram@gmail.com", "Kathmandu", 120),
                new Customer("C002", "Sita Rai", "9800000002", "sita@gmail.com", "Lalitpur", 300),
                new Customer("C003", "John Doe", "9800000003", "john@gmail.com", "Bhaktapur", 80),
                new Customer("C004", "Aarav Singh", "9800000004", "aarav@gmail.com", "Pokhara", 500)
        );
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().toLowerCase();

        if (keyword.isEmpty()) {
            customersTable.setItems(customerList);
            return;
        }

        ObservableList<Customer> filtered = FXCollections.observableArrayList(
                customerList.stream()
                        .filter(c ->
                                c.getName().toLowerCase().contains(keyword) ||
                                        c.getPhone().toLowerCase().contains(keyword) ||
                                        c.getEmail().toLowerCase().contains(keyword)
                        )
                        .collect(Collectors.toList())
        );

        customersTable.setItems(filtered);
    }

    @FXML
    private void onAdd() {
        customerList.add(new Customer(
                "C999",
                "New Customer",
                "9800009999",
                "new@customer.com",
                "Unknown",
                0
        ));
    }

    @FXML
    private void onEdit() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(selected.getName() + " (Updated)");
            customersTable.refresh();
        }
    }

    @FXML
    private void onDelete() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            customerList.remove(selected);
        }
    }

    public static class Customer {

        private final javafx.beans.property.SimpleStringProperty id;
        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleStringProperty phone;
        private final javafx.beans.property.SimpleStringProperty email;
        private final javafx.beans.property.SimpleStringProperty address;
        private final javafx.beans.property.SimpleIntegerProperty loyalty;

        public Customer(String id, String name, String phone, String email, String address, int loyalty) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.phone = new javafx.beans.property.SimpleStringProperty(phone);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.address = new javafx.beans.property.SimpleStringProperty(address);
            this.loyalty = new javafx.beans.property.SimpleIntegerProperty(loyalty);
        }

        public javafx.beans.property.SimpleStringProperty idProperty() { return id; }
        public javafx.beans.property.SimpleStringProperty nameProperty() { return name; }
        public javafx.beans.property.SimpleStringProperty phoneProperty() { return phone; }
        public javafx.beans.property.SimpleStringProperty emailProperty() { return email; }
        public javafx.beans.property.SimpleStringProperty addressProperty() { return address; }
        public javafx.beans.property.SimpleIntegerProperty loyaltyProperty() { return loyalty; }

        public String getName() { return name.get(); }
        public String getPhone() { return phone.get(); }
        public String getEmail() { return email.get(); }

        public void setName(String value) { name.set(value); }
    }
}