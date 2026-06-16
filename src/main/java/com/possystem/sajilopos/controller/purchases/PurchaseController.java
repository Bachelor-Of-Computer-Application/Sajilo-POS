package com.possystem.sajilopos.controller.purchases;

import com.possystem.sajilopos.model.Purchase;
import com.possystem.sajilopos.model.PurchaseItem;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.Supplier;
import com.possystem.sajilopos.service.PurchaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PurchaseController {

    @FXML
    private ComboBox<Supplier> supplierCombo;

    @FXML
    private TextField invoiceField;

    @FXML
    private TextField productSearchField;

    @FXML
    private TextField purchasePriceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<PurchaseItem> itemsTable;

    @FXML
    private TableColumn<PurchaseItem, String> colProduct;

    @FXML
    private TableColumn<PurchaseItem, Double> colPurchasePrice;

    @FXML
    private TableColumn<PurchaseItem, Integer> colQty;

    @FXML
    private TableColumn<PurchaseItem, Double> colTotal;

    @FXML
    private TableColumn<PurchaseItem, Void> colAction;

    @FXML
    private Label grandTotalLabel;

    private final PurchaseService purchaseService = new PurchaseService();
    private Purchase currentPurchase;
    private ObservableList<PurchaseItem> itemsObservable;
    private List<Product> allProducts;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadSuppliers();
        loadProducts();
        setupProductSearch();
        itemsObservable = FXCollections.observableArrayList();
        itemsTable.setItems(itemsObservable);
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colProduct.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProductName()));
        
        colPurchasePrice.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Add delete button to action column
        colAction.setCellFactory(param -> new TableCell<PurchaseItem, Void>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 5px 10px;");
                deleteBtn.setOnAction(event -> {
                    int selectedIndex = getTableRow().getIndex();
                    handleRemoveItemFromTable(selectedIndex);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    /**
     * Load suppliers into combo box
     */
    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = purchaseService.getAllSuppliers();
            ObservableList<Supplier> supplierList = FXCollections.observableArrayList(suppliers);
            supplierCombo.setItems(supplierList);

            // When supplier is selected, initialize new purchase
            supplierCombo.setOnAction(event -> {
                Supplier selected = supplierCombo.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    currentPurchase = purchaseService.createNewPurchase(selected.getSupplierId());
                    invoiceField.setText(currentPurchase.getInvoiceNo());
                    itemsObservable.clear();
                    updateGrandTotal();
                }
            });
        } catch (Exception e) {
            showError("Error loading suppliers: " + e.getMessage());
        }
    }

    /**
     * Load products into memory for search
     */
    private void loadProducts() {
        try {
            allProducts = purchaseService.getAllProducts();
        } catch (Exception e) {
            showError("Error loading products: " + e.getMessage());
        }
    }

    /**
     * Setup product search field with real-time filtering
     */
    private void setupProductSearch() {
        productSearchField.setOnKeyReleased(event -> {
            String searchText = productSearchField.getText().trim();
            if (searchText.isEmpty()) {
                productSearchField.setStyle("-fx-border-color: #cbd5e1; -fx-border-width: 1;");
            } else {
                // Visual feedback for search
                productSearchField.setStyle("-fx-border-color: #3b82f6; -fx-border-width: 2;");
            }
        });
    }

    /**
     * Handle adding product to purchase
     */
    @FXML
    private void handleAddProduct() {
        if (currentPurchase == null) {
            showError("Please select a supplier first");
            return;
        }

        String productName = productSearchField.getText().trim();
        if (productName.isEmpty()) {
            showError("Please enter a product name");
            return;
        }

        try {
            double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity <= 0) {
                showError("Quantity must be greater than 0");
                return;
            }

            if (purchasePrice < 0) {
                showError("Purchase price cannot be negative");
                return;
            }

            // Search for product by name (case-insensitive)
            Product selectedProduct = null;
            for (Product product : allProducts) {
                if (product.getProductName().equalsIgnoreCase(productName)) {
                    selectedProduct = product;
                    break;
                }
            }

            if (selectedProduct == null) {
                showError("Product '" + productName + "' not found. Please check the name.");
                return;
            }

            // Check if product already exists
            for (PurchaseItem item : currentPurchase.getItems()) {
                if (item.getProductId() == selectedProduct.getProductId()) {
                    showError("Product already added to this purchase");
                    return;
                }
            }

            purchaseService.addItemToPurchase(currentPurchase, 
                selectedProduct.getProductId(), 
                purchasePrice, 
                quantity);

            itemsObservable.setAll(currentPurchase.getItems());
            updateGrandTotal();

            // Clear input fields
            productSearchField.clear();
            productSearchField.setStyle("-fx-border-color: #cbd5e1; -fx-border-width: 1;");
            purchasePriceField.clear();
            quantityField.clear();

            showInfo("Product added successfully");
        } catch (NumberFormatException e) {
            showError("Invalid price or quantity format");
        } catch (Exception e) {
            showError("Error adding product: " + e.getMessage());
        }
    }

    /**
     * Handle removing product from table
     */
    private void handleRemoveItemFromTable(int index) {
        if (index >= 0 && index < currentPurchase.getItems().size()) {
            purchaseService.removeItemFromPurchase(currentPurchase, index);
            itemsObservable.setAll(currentPurchase.getItems());
            updateGrandTotal();
            showInfo("Product removed successfully");
        }
    }

    /**
     * Handle removing selected product
     */
    @FXML
    private void handleRemoveProduct() {
        int selectedIndex = itemsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            handleRemoveItemFromTable(selectedIndex);
        } else {
            showError("Please select a product to remove");
        }
    }

    /**
     * Handle saving purchase
     */
    @FXML
    private void handleSavePurchase() {
        if (currentPurchase == null) {
            showError("Please create a purchase first");
            return;
        }

        if (currentPurchase.getItems().isEmpty()) {
            showError("Cannot save purchase without items");
            return;
        }

        try {
            int purchaseId = purchaseService.savePurchase(currentPurchase);
            if (purchaseId > 0) {
                showInfo("Purchase saved successfully! (ID: " + purchaseId + ")");
                handleClear();
            } else {
                showError("Failed to save purchase");
            }
        } catch (Exception e) {
            showError("Error saving purchase: " + e.getMessage());
        }
    }

    /**
     * Handle clearing form
     */
    @FXML
    private void handleClear() {
        supplierCombo.getSelectionModel().clearSelection();
        productSearchField.clear();
        productSearchField.setStyle("-fx-border-color: #cbd5e1; -fx-border-width: 1;");
        invoiceField.clear();
        purchasePriceField.clear();
        quantityField.clear();
        itemsObservable.clear();
        currentPurchase = null;
        updateGrandTotal();
    }

    /**
     * Update grand total display
     */
    private void updateGrandTotal() {
        if (currentPurchase != null) {
            double total = purchaseService.calculateGrandTotal(currentPurchase);
            grandTotalLabel.setText(String.format("Rs. %.2f", total));
        } else {
            grandTotalLabel.setText("Rs. 0.00");
        }
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
