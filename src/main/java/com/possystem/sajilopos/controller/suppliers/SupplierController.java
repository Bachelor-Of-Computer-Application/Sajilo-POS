package com.possystem.sajilopos.controller.suppliers;

import com.possystem.sajilopos.model.Supplier;
import com.possystem.sajilopos.service.SupplierService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SupplierController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextArea addressField;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> colId;

    @FXML
    private TableColumn<Supplier, String> colName;

    @FXML
    private TableColumn<Supplier, String> colPhone;

    @FXML
    private TableColumn<Supplier, String> colAddress;

    private final SupplierService supplierService = new SupplierService();
    private ObservableList<Supplier> suppliersObservable;
    private Supplier selectedSupplier;

    @FXML
    public void initialize() {
        setupTableColumns();
        suppliersObservable = FXCollections.observableArrayList();
        supplierTable.setItems(suppliersObservable);
        loadSuppliers();

        // Allow table row selection
        supplierTable.setOnMouseClicked(event -> {
            Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                populateFields(selected);
                selectedSupplier = selected;
            }
        });
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getSupplierId()).asObject());
        
        colName.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getSupplierName()));
        
        colPhone.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getPhone() != null ? data.getValue().getPhone() : ""));
        
        colAddress.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getAddress() != null ? data.getValue().getAddress() : ""));
    }

    /**
     * Load suppliers into table
     */
    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            System.out.println("Loaded " + suppliers.size() + " suppliers");
            for (Supplier s : suppliers) {
                System.out.println("Supplier: " + s.getSupplierName() + " (ID: " + s.getSupplierId() + ")");
            }
            suppliersObservable.setAll(suppliers);
        } catch (Exception e) {
            System.err.println("Error loading suppliers: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading suppliers: " + e.getMessage());
        }
    }

    /**
     * Populate form fields with supplier data
     */
    private void populateFields(Supplier supplier) {
        nameField.setText(supplier.getSupplierName());
        phoneField.setText(supplier.getPhone() != null ? supplier.getPhone() : "");
        addressField.setText(supplier.getAddress() != null ? supplier.getAddress() : "");
    }

    /**
     * Clear form fields
     */
    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        addressField.clear();
        selectedSupplier = null;
        supplierTable.getSelectionModel().clearSelection();
    }

    /**
     * Handle adding new supplier
     */
    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {
            showError("Please enter supplier name");
            return;
        }

        try {
            boolean success = supplierService.addSupplier(name, phone, address);
            if (success) {
                showInfo("Supplier added successfully");
                loadSuppliers();
                clearFields();
            } else {
                showError("Failed to add supplier");
            }
        } catch (Exception e) {
            showError("Error adding supplier: " + e.getMessage());
        }
    }

    /**
     * Handle updating supplier
     */
    @FXML
    private void handleUpdate() {
        if (selectedSupplier == null) {
            showError("Please select a supplier to update");
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {
            showError("Please enter supplier name");
            return;
        }

        try {
            boolean success = supplierService.updateSupplier(
                selectedSupplier.getSupplierId(),
                name,
                phone,
                address
            );
            if (success) {
                showInfo("Supplier updated successfully");
                loadSuppliers();
                clearFields();
            } else {
                showError("Failed to update supplier");
            }
        } catch (Exception e) {
            showError("Error updating supplier: " + e.getMessage());
        }
    }

    /**
     * Handle deleting supplier
     */
    @FXML
    private void handleDelete() {
        if (selectedSupplier == null) {
            showError("Please select a supplier to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete this supplier?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean success = supplierService.deleteSupplier(selectedSupplier.getSupplierId());
                if (success) {
                    showInfo("Supplier deleted successfully");
                    loadSuppliers();
                    clearFields();
                } else {
                    showError("Failed to delete supplier");
                }
            } catch (Exception e) {
                showError("Error deleting supplier: " + e.getMessage());
            }
        }
    }

    /**
     * Handle searching suppliers
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        
        try {
            if (searchTerm.isEmpty()) {
                loadSuppliers();
            } else {
                List<Supplier> results = supplierService.searchSuppliers(searchTerm);
                suppliersObservable.setAll(results);
            }
        } catch (Exception e) {
            showError("Error searching suppliers: " + e.getMessage());
        }
    }

    /**
     * Handle refresh
     */
    @FXML
    private void handleRefresh() {
        loadSuppliers();
        clearFields();
        searchField.clear();
    }

    /**
     * Handle clear form
     */
    @FXML
    private void handleClear() {
        clearFields();
    }

    /**
     * Show error dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info dialog
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
