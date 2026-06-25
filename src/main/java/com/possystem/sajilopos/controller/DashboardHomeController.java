package com.possystem.sajilopos.controller.dashboard;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.dao.ReportDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class DashboardHomeController {

    @FXML private Label totalSalesLabel;
    @FXML private Label totalTransactionsLabel;
    @FXML private Label lowStockLabel;

    private final ReportDAO reportDAO = new ReportDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        LocalDate today = LocalDate.now();

        try {
            double todaySales = reportDAO.getTotalSales(today, today);
            totalSalesLabel.setText(String.format("Rs. %.2f", todaySales));
        } catch (Exception e) {
            totalSalesLabel.setText("Rs. 0.00");
            System.err.println("Dashboard stats error: " + e.getMessage());
        }

        try {
            int transactions = reportDAO.getTotalTransactions(today, today);
            totalTransactionsLabel.setText(String.valueOf(transactions));
        } catch (Exception e) {
            totalTransactionsLabel.setText("0");
        }

        try {
            int lowStock = productDAO.getLowStockProducts(companyId).size();
            lowStockLabel.setText(String.valueOf(lowStock));
            if (lowStock > 0) {
                lowStockLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");
            }
        } catch (Exception e) {
            lowStockLabel.setText("0");
        }
    }
}
