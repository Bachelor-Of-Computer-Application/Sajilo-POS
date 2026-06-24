package com.possystem.sajilopos.controller.sales;

import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.SaleItem;
import com.possystem.sajilopos.service.BillingService;
import com.possystem.sajilopos.service.ProductService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class salescontroller {

    @FXML private TableView<SaleItem> cartTable;
    @FXML private TableColumn<SaleItem, String> productCol;
    @FXML private TableColumn<SaleItem, Integer> qtyCol;
    @FXML private TableColumn<SaleItem, Double> priceCol;

    @FXML private TextField barcodeField;
    @FXML private Label totalLabel;

    private final ObservableList<SaleItem> cartItems = FXCollections.observableArrayList();
    private final BillingService billingService = new BillingService();
    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        // Wire up table columns
        productCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProduct().getProductName()));

        qtyCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        priceCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        cartTable.setItems(cartItems);
        updateTotal();
    }

    @FXML
    private void addItem() {
        String input = barcodeField.getText().trim();
        if (input.isEmpty()) {
            showWarning("Input required", "Please enter a product ID or name.");
            return;
        }

        try {
            // Try by numeric ID first
            int productId = Integer.parseInt(input);
            String result = billingService.addItem(productId, 1);
            refreshCart();
            barcodeField.clear();
            if (result.contains("not found")) {
                showWarning("Not Found", result);
            }
        } catch (NumberFormatException e) {
            // Try search by name
            try {
                var results = productService.searchProducts(input);
                if (results.isEmpty()) {
                    showWarning("Not Found", "No product found for: " + input);
                } else {
                    Product p = results.get(0);
                    String result = billingService.addItem(p.getProductId(), 1);
                    refreshCart();
                    barcodeField.clear();
                    if (result.contains("not found") || result.contains("Insufficient")) {
                        showWarning("Error", result);
                    }
                }
            } catch (Exception ex) {
                showError("Error searching product: " + ex.getMessage());
            }
        } catch (Exception e) {
            showError("Error adding item: " + e.getMessage());
        }
    }

    @FXML
    private void checkout() {
        if (billingService.getItemCount() == 0) {
            showWarning("Empty Cart", "Please add items before checkout.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Checkout");
        confirm.setHeaderText("Total: Rs. " + String.format("%.2f", billingService.getTotal()));
        confirm.setContentText("Proceed with checkout?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = billingService.processSale(0.0);
                if (success) {
                    showInfo("Sale completed successfully!");
                    refreshCart();
                } else {
                    showError("Checkout failed. Please try again.");
                }
            }
        });
    }

    private void refreshCart() {
        cartItems.clear();
        cartItems.addAll(billingService.getCurrentItems());
        updateTotal();
    }

    private void updateTotal() {
        totalLabel.setText("Total: Rs. " + String.format("%.2f", billingService.getTotal()));
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
