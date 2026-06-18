package com.possystem.sajilopos.controller.dashboard;

import com.possystem.sajilopos.config.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML private BorderPane rootPane;

    @FXML private Button productsMenu;
    @FXML private Button inventoryMenu;
    @FXML private Button purchasesMenu;
    @FXML private Button suppliersMenu;
    @FXML private Button reportsMenu;
    @FXML private Button usersMenu;
    @FXML private Button settingsMenu;

    private final SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    public void initialize() {
        applyRoleBasedAccess();
        openDashboard();
    }

    private void applyRoleBasedAccess() {
        if (sessionManager.isCashier()) {
            hideMenu(productsMenu);
            hideMenu(inventoryMenu);
            hideMenu(purchasesMenu);
            hideMenu(suppliersMenu);
            hideMenu(reportsMenu);
            hideMenu(usersMenu);
            hideMenu(settingsMenu);
        } else if (sessionManager.isManager()) {
            hideMenu(usersMenu);
            hideMenu(settingsMenu);
        }
    }

    private void hideMenu(Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    private void loadView(String path) {
        try {
            rootPane.setCenter(FXMLLoader.load(getClass().getResource(path)));
        } catch (Exception e) {
            Label errorLabel = new Label("Error loading " + path + "\n" + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20;");
            rootPane.setCenter(errorLabel);
        }
    }

    @FXML private void openDashboard() { loadView("/fxml/dashboard/dashboard-home.fxml"); }
    @FXML private void openProducts() { loadView("/fxml/product/product.fxml"); }
    @FXML private void openCustomers() { loadView("/fxml/customers/customers.fxml"); }
    @FXML private void openSuppliers() { loadView("/fxml/suppliers/suppliers.fxml"); }
    @FXML private void openReports() { loadView("/fxml/reports/reports.fxml"); }
    @FXML private void openSales() { loadView("/fxml/sales/sales.fxml"); }
    @FXML private void openInventory() { loadView("/fxml/inventory/inventory.fxml"); }
    @FXML private void openPurchases() { loadView("/fxml/purchases/purchases.fxml"); }
    @FXML private void openPurchaseHistory() { loadView("/fxml/purchases/purchaseHistory.fxml"); }
    @FXML private void openSettings() { loadView("/fxml/settings/settings.fxml"); }
    @FXML private void openUsers() { loadView("/fxml/users/users.fxml"); }
}
