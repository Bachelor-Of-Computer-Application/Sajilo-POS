package com.possystem.sajilopos.controller.purchases;

import com.possystem.sajilopos.model.Purchase;
import com.possystem.sajilopos.model.PurchaseItem;
import com.possystem.sajilopos.service.PurchaseService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseHistoryController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PurchaseHistoryItem> purchaseTable;

    @FXML
    private TableColumn<PurchaseHistoryItem, String> colInvoice;

    @FXML
    private TableColumn<PurchaseHistoryItem, String> colSupplier;

    @FXML
    private TableColumn<PurchaseHistoryItem, String> colProduct;

    @FXML
    private TableColumn<PurchaseHistoryItem, Integer> colQty;

    @FXML
    private TableColumn<PurchaseHistoryItem, Double> colPurchasePrice;

    @FXML
    private TableColumn<PurchaseHistoryItem, Double> colTotal;

    @FXML
    private TableColumn<PurchaseHistoryItem, String> colDate;

    private final PurchaseService purchaseService = new PurchaseService();
    private ObservableList<PurchaseHistoryItem> purchaseHistoryObservable;
    private List<PurchaseHistoryItem> allPurchaseItems;

    @FXML
    public void initialize() {
        setupTableColumns();
        purchaseHistoryObservable = FXCollections.observableArrayList();
        purchaseTable.setItems(purchaseHistoryObservable);
        loadPurchaseHistory();

        // Live search: filter as user types
        searchField.setOnKeyReleased(event -> handleSearch());
    }

    /**
     * Setup table columns with SimpleProperty bindings
     */
    private void setupTableColumns() {
        colInvoice.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getInvoiceNo()));

        colSupplier.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getSupplierName()));

        colProduct.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProductName()));

        colQty.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        colPurchasePrice.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getPurchasePrice()).asObject());

        colTotal.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getTotal()).asObject());

        colDate.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPurchaseDate()));
    }

    /**
     * Load purchase history from database
     */
    private void loadPurchaseHistory() {
        try {
            List<Purchase> purchases = purchaseService.getPurchaseHistory();
            allPurchaseItems = new ArrayList<>();

            // Flatten purchases into items for table display
            for (Purchase purchase : purchases) {
                String supplierName = getSupplierName(purchase.getSupplierId());
                String purchaseDate = formatDate(purchase.getPurchaseDate());

                for (PurchaseItem item : purchase.getItems()) {
                    allPurchaseItems.add(new PurchaseHistoryItem(
                        purchase.getInvoiceNo(),
                        supplierName,
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPurchasePrice(),
                        item.getTotal(),
                        purchaseDate,
                        purchase.getPurchaseId()
                    ));
                }
            }

            purchaseHistoryObservable.setAll(allPurchaseItems);
            System.out.println("Loaded " + allPurchaseItems.size() + " purchase items");
        } catch (Exception e) {
            System.err.println("Error loading purchase history: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading purchase history: " + e.getMessage());
        }
    }

    /**
     * Get supplier name by supplier ID
     */
    private String getSupplierName(int supplierId) {
        try {
            List<com.possystem.sajilopos.model.Supplier> suppliers = purchaseService.getAllSuppliers();
            for (com.possystem.sajilopos.model.Supplier supplier : suppliers) {
                if (supplier.getSupplierId() == supplierId) {
                    return supplier.getSupplierName();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting supplier name: " + e.getMessage());
        }
        return "N/A";
    }

    /**
     * Format date for display
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    /**
     * Handle search functionality
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        try {
            if (searchTerm.isEmpty()) {
                purchaseHistoryObservable.setAll(allPurchaseItems);
            } else {
                List<PurchaseHistoryItem> filtered = new ArrayList<>();
                for (PurchaseHistoryItem item : allPurchaseItems) {
                    if (item.getInvoiceNo().toLowerCase().contains(searchTerm) ||
                        item.getSupplierName().toLowerCase().contains(searchTerm) ||
                        item.getProductName().toLowerCase().contains(searchTerm)) {
                        filtered.add(item);
                    }
                }
                purchaseHistoryObservable.setAll(filtered);
            }
        } catch (Exception e) {
            showError("Error searching: " + e.getMessage());
        }
    }

    /**
     * Handle refresh
     */
    @FXML
    private void handleRefresh() {
        loadPurchaseHistory();
        searchField.clear();
    }

    /**
     * Handle view details (placeholder for future enhancement)
     */
    @FXML
    private void handleViewDetails() {
        PurchaseHistoryItem selected = purchaseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a purchase to view details");
            return;
        }

        showInfo("Invoice: " + selected.getInvoiceNo() + "\nSupplier: " + selected.getSupplierName());
    }

    /**
     * Handle delete purchase
     */
    @FXML
    private void handleDelete() {
        PurchaseHistoryItem selected = purchaseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a purchase to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setContentText("Are you sure you want to delete this purchase?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                boolean success = purchaseService.deletePurchase(selected.getPurchaseId());
                if (success) {
                    showInfo("Purchase deleted successfully");
                    loadPurchaseHistory();
                } else {
                    showError("Failed to delete purchase");
                }
            } catch (Exception e) {
                showError("Error deleting purchase: " + e.getMessage());
            }
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
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Inner class to represent a purchase history item for table display
     */
    public static class PurchaseHistoryItem {
        private final String invoiceNo;
        private final String supplierName;
        private final String productName;
        private final int quantity;
        private final double purchasePrice;
        private final double total;
        private final String purchaseDate;
        private final int purchaseId;

        public PurchaseHistoryItem(String invoiceNo, String supplierName, String productName,
                                  int quantity, double purchasePrice, double total,
                                  String purchaseDate, int purchaseId) {
            this.invoiceNo = invoiceNo;
            this.supplierName = supplierName;
            this.productName = productName;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
            this.total = total;
            this.purchaseDate = purchaseDate;
            this.purchaseId = purchaseId;
        }

        public String getInvoiceNo() { return invoiceNo; }
        public String getSupplierName() { return supplierName; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPurchasePrice() { return purchasePrice; }
        public double getTotal() { return total; }
        public String getPurchaseDate() { return purchaseDate; }
        public int getPurchaseId() { return purchaseId; }
    }
}
