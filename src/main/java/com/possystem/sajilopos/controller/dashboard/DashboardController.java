package com.possystem.sajilopos.controller.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    private void loadView(String path) {
        try {
            System.out.println("Loading view: " + path);
            rootPane.setCenter(
                    FXMLLoader.load(getClass().getResource(path))
            );
            System.out.println("View loaded successfully: " + path);
        } catch (Exception e) {
            System.err.println("Error loading view: " + path);
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            
            // Show error in the center pane
            Label errorLabel = new Label("Error loading " + path + "\n" + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20;");
            rootPane.setCenter(errorLabel);
        }
    }

    @FXML
    private void openDashboard() {
        loadView("/fxml/dashboard/dashboard-home.fxml");
    }

    @FXML
    private void openProducts() {
        loadView("/fxml/product/product.fxml");
    }

    @FXML
    private void openCustomers() {
        loadView("/fxml/customers/customers.fxml");
    }

    @FXML
    private void openSuppliers() {
        loadView("/fxml/suppliers/suppliers.fxml");
    }

    @FXML
    private void openReports() {
        loadView("/fxml/reports/reports.fxml");
    }

    @FXML
    private void openSales() {
        loadView("/fxml/sales/sales.fxml");
    }
    
    @FXML
    private void openInventory() {
        loadView("/fxml/inventory/inventory.fxml");
    }

    @FXML
    private void openPurchases() {
        loadView("/fxml/purchases/purchases.fxml");
    }

    @FXML
    private void openPurchaseHistory() {
        loadView("/fxml/purchases/purchaseHistory.fxml");
    }

    @FXML
    private void openSettings() {
        loadView("/fxml/settings/settings.fxml");
    }

    @FXML
    private void openUsers() {
        loadView("/fxml/users/users.fxml");
    }
}