package com.possystem.sajilopos.controller.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML
    private BorderPane rootPane;

    private void loadView(String path) {
        try {
            rootPane.setCenter(
                    FXMLLoader.load(getClass().getResource(path))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDashboard() {
        loadView("/fxml/dashboard/home.fxml");
    }

    @FXML
    private void openProducts() {
        loadView("/fxml/products/products.fxml");
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
        loadView("/fxml/sales/pos.fxml");
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
    private void openSettings() {
        loadView("/fxml/settings/settings.fxml");
    }

    @FXML
    private void openUsers() {
        loadView("/fxml/users/users.fxml");
    }
}