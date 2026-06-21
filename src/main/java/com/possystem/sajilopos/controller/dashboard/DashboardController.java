package com.possystem.sajilopos.controller.dashboard;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private BorderPane rootPane;

    @FXML private Label companyNameLabel;
    @FXML private Label sidebarCompanyLabel;
    @FXML private Label roleLabel;

    @FXML private Label totalSalesLabel;
    @FXML private Label productCountLabel;
    @FXML private Label customerCountLabel;
    @FXML private Label supplierCountLabel;

    @FXML private Button dashboardMenu;
    @FXML private Button productsMenu;
    @FXML private Button customersMenu;
    @FXML private Button inventoryMenu;
    @FXML private Button purchasesMenu;
    @FXML private Button purchaseHistoryMenu;
    @FXML private Button suppliersMenu;
    @FXML private Button salesMenu;
    @FXML private Button reportsMenu;
    @FXML private Button usersMenu;
    @FXML private Button settingsMenu;

    @FXML
    public void initialize() {

        User user = SessionManager.getInstance().getCurrentUser();

        if (user != null) {
            roleLabel.setText("Logged in as: " + user.getRole());

            companyNameLabel.setText("Company ID : " + user.getCompanyId());
            sidebarCompanyLabel.setText("Company ID : " + user.getCompanyId());
        }

        loadStatistics();
        applyRolePermissions();

        openDashboard(); // default screen
    }

    // 🔥 CORE NAVIGATION ENGINE (THIS IS THE MAGIC)
    private void loadCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            rootPane.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStatistics() {
        totalSalesLabel.setText("Rs. 0");
        productCountLabel.setText("0");
        customerCountLabel.setText("0");
        supplierCountLabel.setText("0");
    }

    private void applyRolePermissions() {

        SessionManager session = SessionManager.getInstance();

        if (session.isCashier()) {
            inventoryMenu.setVisible(false);
            purchasesMenu.setVisible(false);
            purchaseHistoryMenu.setVisible(false);
            suppliersMenu.setVisible(false);
            reportsMenu.setVisible(false);
            usersMenu.setVisible(false);
            settingsMenu.setVisible(false);
        }

        if (session.isManager()) {
            usersMenu.setVisible(false);
            settingsMenu.setVisible(false);
        }
    }

    // =========================
    // NAVIGATION METHODS
    // =========================

    @FXML
    private void openDashboard() {
        loadCenter("/fxml/dashboard/dashboard-home.fxml");
    }

    @FXML
    private void openProducts() {
        loadCenter("/fxml/product/product.fxml");
    }

    @FXML
    private void openCustomers() {
        loadCenter("/fxml/customers/customers.fxml");
    }

    @FXML
    private void openInventory() {
        loadCenter("/fxml/inventory/inventory.fxml");
    }

    @FXML
    private void openPurchases() {
        loadCenter("/fxml/purchases/purchases.fxml");
    }

    @FXML
    private void openPurchaseHistory() {
        loadCenter("/fxml/purchases/purchaseHistory.fxml");
    }

    @FXML
    private void openSuppliers() {
        loadCenter("/fxml/suppliers/suppliers.fxml");
    }

    @FXML
    private void openSales() {
        loadCenter("/fxml/sales/sales.fxml");
    }

    @FXML
    private void openReports() {
        loadCenter("/fxml/reports/reports.fxml");
    }

    @FXML
    private void openUsers() {
        loadCenter("/fxml/users/users.fxml");
    }

    @FXML
    private void openSettings() {
        loadCenter("/fxml/settings/settings.fxml");
    }

    // =========================
    // LOGOUT
    // =========================

    @FXML
    private void handleLogout() {
        try {
            SessionManager.getInstance().logout();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/auth/login.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sajilo POS");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}