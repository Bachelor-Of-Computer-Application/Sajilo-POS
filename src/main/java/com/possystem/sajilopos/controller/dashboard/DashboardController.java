package com.possystem.sajilopos.controller.dashboard;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.CompanyDAO;
import com.possystem.sajilopos.model.Company;
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

    private final CompanyDAO companyDAO = new CompanyDAO();

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        User currentUser = session.getCurrentUser();

        if (currentUser != null) {
            String username = currentUser.getUsername() != null ? currentUser.getUsername() : "Unknown";
            String role     = currentUser.getRole()     != null ? currentUser.getRole()     : "CASHIER";
            roleLabel.setText("User: " + username + " | Role: " + role);

            // Fetch real company name from DB instead of showing raw ID
            Company company = companyDAO.getCompanyById(currentUser.getCompanyId());
            String companyDisplay = (company != null) ? company.getCompanyName()
                                                      : "Company #" + currentUser.getCompanyId();
            companyNameLabel.setText(companyDisplay);
            sidebarCompanyLabel.setText(companyDisplay);
        } else {
            roleLabel.setText("Not logged in");
            companyNameLabel.setText("");
            sidebarCompanyLabel.setText("");
        }

        applyRolePermissions();

        // Load dashboard home by default
        openDashboard();
    }

    private void applyRolePermissions() {
        SessionManager session = SessionManager.getInstance();

        // CASHIER: only Sales and Customers visible
        if (session.isCashier()) {
            setHidden(inventoryMenu);
            setHidden(purchasesMenu);
            setHidden(purchaseHistoryMenu);
            setHidden(suppliersMenu);
            setHidden(reportsMenu);
            setHidden(usersMenu);
            // Settings stays visible — cashiers need to change their own password
        }

        // MANAGER: everything except Users and Settings (settings for password change)
        if (session.isManager()) {
            setHidden(usersMenu);
            // Settings stays visible — managers need to change their own password
        }

        // ADMIN: sees everything — no changes needed
    }

    private void setHidden(Button button) {
        if (button != null) {
            button.setVisible(false);
            button.setManaged(false);
        }
    }

    private void loadView(String path) {
        try {
            rootPane.setCenter(FXMLLoader.load(getClass().getResource(path)));
        } catch (Exception e) {
            System.err.println("Error loading view: " + path + " — " + e.getMessage());
            e.printStackTrace();
            Label errorLabel = new Label("Error loading view:\n" + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-padding: 20;");
            rootPane.setCenter(errorLabel);
        }
    }

    // ── Navigation ──────────────────────────────────────────────────────────

    @FXML private void openDashboard()       { loadView("/fxml/dashboard/dashboard-home.fxml"); }
    @FXML private void openProducts()        { loadView("/fxml/product/product.fxml"); }
    @FXML private void openCustomers()       { loadView("/fxml/customers/customers.fxml"); }
    @FXML private void openSuppliers()       { loadView("/fxml/suppliers/suppliers.fxml"); }
    @FXML private void openReports()         { loadView("/fxml/reports/reports.fxml"); }
    @FXML private void openSales()           { loadView("/fxml/sales/sales.fxml"); }
    @FXML private void openInventory()       { loadView("/fxml/inventory/inventory.fxml"); }
    @FXML private void openPurchases()       { loadView("/fxml/purchases/purchases.fxml"); }
    @FXML private void openPurchaseHistory() { loadView("/fxml/purchases/purchaseHistory.fxml"); }
    @FXML private void openSettings()        { loadView("/fxml/settings/settings.fxml"); }
    @FXML private void openUsers()           { loadView("/fxml/users/users.fxml"); }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.getInstance().logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) rootPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Sajilo POS - Login");
            stage.setMaximized(false);
            stage.setWidth(860);
            stage.setHeight(520);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
Category.java — add companyId field
CategoryDAO.java — fix queries to filter by company_id
Product.java — add categoryId field
ProductDAO.java — include category_id in all queries
ProductService.java — pass categoryId through
ProductController.java — add category ComboBox
product.fxml — add Category dropdown + Category column in table
