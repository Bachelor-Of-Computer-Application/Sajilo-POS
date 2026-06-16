package com.possystem.sajilopos.controller.customers;

import com.possystem.sajilopos.model.Customer;
import com.possystem.sajilopos.service.CustomerService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;
    @FXML private TextField loyaltyField;
    @FXML private TextField searchField;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colAddress;
    @FXML private TableColumn<Customer, Integer> colLoyalty;

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final CustomerService customerService = new CustomerService();
    private Customer selectedCustomer = null;

    @FXML
    public void initialize() {
        // Set up table columns
        colId.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getCustomerId()).asObject());
        
        colName.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getCustomerName()));
        
        colPhone.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getPhone()));
        
        colAddress.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getAddress()));
        
        colLoyalty.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getLoyaltyPoints()).asObject());

        customerTable.setItems(customers);

        // Load customers from database
        loadCustomers();

        // Set up row selection listener
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCustomer = newSelection;
                populateFields(newSelection);
            }
        });
    }

    /**
     * Load all customers from database
     */
    private void loadCustomers() {
        try {
            customers.clear();
            customers.addAll(customerService.getAllCustomers());
            System.out.println("Loaded " + customers.size() + " customers");
        } catch (Exception e) {
            showError("Error Loading Customers", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Populate form fields with selected customer data
     */
    private void populateFields(Customer customer) {
        nameField.setText(customer.getCustomerName());
        phoneField.setText(customer.getPhone());
        addressArea.setText(customer.getAddress());
        if (loyaltyField != null) {
            loyaltyField.setText(String.valueOf(customer.getLoyaltyPoints()));
        }
    }

    /**
     * Clear all form fields
     */
    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        addressArea.clear();
        if (loyaltyField != null) {
            loyaltyField.setText("0");
        }
        selectedCustomer = null;
        customerTable.getSelectionModel().clearSelection();
    }

    /**
     * Handle Add Customer button click
     */
    @FXML
    private void handleAdd() {
        try {
            // Get input values
            String name = nameField.getText();
            String phone = phoneField.getText();
            String address = addressArea.getText();

            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                showWarning("Validation Error", "Customer name is required");
                return;
            }

            if (phone == null || phone.trim().isEmpty()) {
                showWarning("Validation Error", "Phone number is required");
                return;
            }

            // Add customer (loyalty points automatically set to 0)
            boolean success = customerService.addCustomer(name, phone, address);

            if (success) {
                showInfo("Success", "Customer added successfully with 0 loyalty points");
                loadCustomers();
                clearFields();
            } else {
                showError("Error", "Failed to add customer");
            }

        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to add customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Update Customer button click
     */
    @FXML
    private void handleUpdate() {
        if (selectedCustomer == null) {
            showWarning("No Selection", "Please select a customer to update");
            return;
        }

        try {
            // Get input values
            String name = nameField.getText();
            String phone = phoneField.getText();
            String address = addressArea.getText();

            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                showWarning("Validation Error", "Customer name is required");
                return;
            }

            if (phone == null || phone.trim().isEmpty()) {
                showWarning("Validation Error", "Phone number is required");
                return;
            }

            // Update customer (loyalty points are preserved automatically)
            boolean success = customerService.updateCustomer(
                selectedCustomer.getCustomerId(), name, phone, address
            );

            if (success) {
                showInfo("Success", "Customer updated successfully");
                loadCustomers();
                clearFields();
            } else {
                showError("Error", "Failed to update customer");
            }

        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to update customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Delete Customer button click
     */
    @FXML
    private void handleDelete() {
        if (selectedCustomer == null) {
            showWarning("No Selection", "Please select a customer to delete");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Customer");
        confirmAlert.setContentText("Are you sure you want to delete '" + selectedCustomer.getCustomerName() + "'?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean success = customerService.deleteCustomer(selectedCustomer.getCustomerId());

                if (success) {
                    showInfo("Success", "Customer deleted successfully");
                    loadCustomers();
                    clearFields();
                } else {
                    showError("Error", "Failed to delete customer");
                }

            } catch (Exception e) {
                showError("Error", "Failed to delete customer: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle Search button click
     */
    @FXML
    private void handleSearch() {
        try {
            String searchText = searchField.getText();
            customers.clear();
            customers.addAll(customerService.searchCustomers(searchText));
            System.out.println("Found " + customers.size() + " customers matching '" + searchText + "'");
        } catch (Exception e) {
            showError("Search Error", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Clear/Reset button click
     */
    @FXML
    private void handleClear() {
        clearFields();
    }

    /**
     * Handle Refresh button click
     */
    @FXML
    private void handleRefresh() {
        loadCustomers();
        searchField.clear();
    }

    // Helper methods for showing alerts
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
