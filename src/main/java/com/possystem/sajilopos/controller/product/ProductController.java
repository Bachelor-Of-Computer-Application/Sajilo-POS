package com.possystem.sajilopos.controller.product;

import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.service.ProductService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField searchField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colDescription;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ProductService productService = new ProductService();
    private Product selectedProduct = null;

    @FXML
    public void initialize() {
        // Set up table columns
        colId.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getProductId()).asObject());
        
        colName.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProductName()));
        
        colPrice.setCellValueFactory(data -> 
            new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        
        colStock.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        
        colDescription.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDescription()));

        productTable.setItems(products);

        // Load products from database
        loadProducts();

        // Set up row selection listener
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                populateFields(newSelection);
            }
        });
    }

    /**
     * Load all products from database
     */
    private void loadProducts() {
        try {
            products.clear();
            products.addAll(productService.getAllProducts());
            System.out.println("Loaded " + products.size() + " products");
        } catch (Exception e) {
            showError("Error Loading Products", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Populate form fields with selected product data
     */
    private void populateFields(Product product) {
        nameField.setText(product.getProductName());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
        descriptionArea.setText(product.getDescription());
    }

    /**
     * Clear all form fields
     */
    private void clearFields() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
        descriptionArea.clear();
        selectedProduct = null;
        productTable.getSelectionModel().clearSelection();
    }

    /**
     * Handle Add Product button click
     */
    @FXML
    private void handleAdd() {
        try {
            // Get input values
            String name = nameField.getText();
            String priceText = priceField.getText();
            String stockText = stockField.getText();
            String description = descriptionArea.getText();

            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                showWarning("Validation Error", "Product name is required");
                return;
            }

            if (priceText == null || priceText.trim().isEmpty()) {
                showWarning("Validation Error", "Price is required");
                return;
            }

            if (stockText == null || stockText.trim().isEmpty()) {
                showWarning("Validation Error", "Stock quantity is required");
                return;
            }

            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            // Add product
            boolean success = productService.addProduct(name, price, stock, description);

            if (success) {
                showInfo("Success", "Product added successfully");
                loadProducts();
                clearFields();
            } else {
                showError("Error", "Failed to add product");
            }

        } catch (NumberFormatException e) {
            showWarning("Invalid Input", "Price and Stock must be valid numbers");
        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to add product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Update Product button click
     */
    @FXML
    private void handleUpdate() {
        if (selectedProduct == null) {
            showWarning("No Selection", "Please select a product to update");
            return;
        }

        try {
            // Get input values
            String name = nameField.getText();
            String priceText = priceField.getText();
            String stockText = stockField.getText();
            String description = descriptionArea.getText();

            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                showWarning("Validation Error", "Product name is required");
                return;
            }

            if (priceText == null || priceText.trim().isEmpty()) {
                showWarning("Validation Error", "Price is required");
                return;
            }

            if (stockText == null || stockText.trim().isEmpty()) {
                showWarning("Validation Error", "Stock quantity is required");
                return;
            }

            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            // Update product
            boolean success = productService.updateProduct(
                selectedProduct.getProductId(), name, price, stock, description
            );

            if (success) {
                showInfo("Success", "Product updated successfully");
                loadProducts();
                clearFields();
            } else {
                showError("Error", "Failed to update product");
            }

        } catch (NumberFormatException e) {
            showWarning("Invalid Input", "Price and Stock must be valid numbers");
        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to update product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Delete Product button click
     */
    @FXML
    private void handleDelete() {
        if (selectedProduct == null) {
            showWarning("No Selection", "Please select a product to delete");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Product");
        confirmAlert.setContentText("Are you sure you want to delete '" + selectedProduct.getProductName() + "'?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean success = productService.deleteProduct(selectedProduct.getProductId());

                if (success) {
                    showInfo("Success", "Product deleted successfully");
                    loadProducts();
                    clearFields();
                } else {
                    showError("Error", "Failed to delete product");
                }

            } catch (Exception e) {
                showError("Error", "Failed to delete product: " + e.getMessage());
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
            products.clear();
            products.addAll(productService.searchProducts(searchText));
            System.out.println("Found " + products.size() + " products matching '" + searchText + "'");
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
        loadProducts();
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