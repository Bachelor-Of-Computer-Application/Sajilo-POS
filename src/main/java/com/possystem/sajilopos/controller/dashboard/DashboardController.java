package com.possystem.sajilopos.controller.dashboard;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML private BorderPane rootPane;
    @FXML private Label roleLabel;
    @FXML private Label companyNameLabel;
    @FXML private Label sidebarCompanyLabel;
    @FXML private Button dashboardMenu;
    @FXML private Button customersMenu;
    @FXML private Button purchaseHistoryMenu;
    @FXML private Button inventoryMenu;
    @FXML private Button purchasesMenu;
    @FXML private Button suppliersMenu;
    @FXML private Button reportsMenu;
    @FXML private Button usersMenu;
    @FXML private Button settingsMenu;

    @FXML
    public void initialize() {
        // Load current user session info
        SessionManager session = SessionManager.getInstance();
        User currentUser = session.getCurrentUser();
        
        if (currentUser != null) {
            String username = currentUser.getUsername() != null ? currentUser.getUsername() : "Unknown";
            String role = currentUser.getRole() != null ? currentUser.getRole() : "CASHIER";
            roleLabel.setText("User: " + username + " | Role: " + role);
            String companyDisplay = "Company ID: " + currentUser.getCompanyId();
            companyNameLabel.setText(companyDisplay);
            sidebarCompanyLabel.setText(companyDisplay);
        } else {
            roleLabel.setText("Not logged in");
            companyNameLabel.setText("Company ID: N/A");
            sidebarCompanyLabel.setText("Company ID: N/A");
        }
        
        // Apply role-based permissions
        applyRolePermissions();
    }

    private void applyRolePermissions() {
        SessionManager session = SessionManager.getInstance();

        if (session.isCashier()) {
            setHidden(inventoryMenu);
            setHidden(purchasesMenu);
            setHidden(purchaseHistoryMenu);
            setHidden(suppliersMenu);
            setHidden(reportsMenu);
            setHidden(usersMenu);
            setHidden(settingsMenu);
        }

        if (session.isManager()) {
            setHidden(usersMenu);
            setHidden(settingsMenu);
        }
    }

    private void setHidden(Button button) {
        button.setVisible(false);
        button.setManaged(false);
    }

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

    // =========================
    // NAVIGATION METHODS
    // =========================

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

    @FXML
    private void handleLogout() {
        try {
            SessionManager.getInstance().logout();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/auth/login.fxml")
            );
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Sajilo POS - Login");
            stage.setMaximized(false);
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
