package com.possystem.sajilopos.controller.dashboard;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.ReportDAO;
import com.possystem.sajilopos.dao.SaleDAO;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SummaryReportDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardHomeController {

    @FXML private Label totalSalesLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label totalCustomersLabel;

    @FXML private Label invoice1Label;
    @FXML private Label invoice2Label;
    @FXML private Label invoice3Label;
    @FXML private Label noSalesLabel;

    private final ReportDAO reportDAO = new ReportDAO();
    private final SaleDAO   saleDAO   = new SaleDAO();
    private final SessionManager session = SessionManager.getInstance();

    @FXML
    public void initialize() {
        loadSummaryCards();
        loadRecentSales();
    }

    private void loadSummaryCards() {
        try {
            int companyId = session.getCurrentCompanyId();
            LocalDate today = LocalDate.now();

            // Today's summary
            SummaryReportDTO summary = reportDAO.getSummaryReport(today, today);
            if (summary != null) {
                totalSalesLabel.setText(String.format("Rs. %.0f", summary.getTotalSales()));
                totalProductsLabel.setText(String.valueOf(summary.getTotalProductsSold()));
                totalCustomersLabel.setText(String.valueOf(summary.getTotalTransactions()));
            }
        } catch (Exception e) {
            System.err.println("Dashboard summary error: " + e.getMessage());
        }
    }

    private void loadRecentSales() {
        try {
            int companyId = session.getCurrentCompanyId();
            List<Sale> recent = saleDAO.getRecentSales(companyId, 3);

            Label[] labels = { invoice1Label, invoice2Label, invoice3Label };

            if (recent.isEmpty()) {
                noSalesLabel.setVisible(true);
                noSalesLabel.setManaged(true);
                for (Label l : labels) { l.setVisible(false); l.setManaged(false); }
                return;
            }

            noSalesLabel.setVisible(false);
            noSalesLabel.setManaged(false);

            for (int i = 0; i < labels.length; i++) {
                if (i < recent.size()) {
                    Sale s = recent.get(i);
                    String date = s.getSaleDate() != null
                            ? s.getSaleDate().toLocalDateTime()
                                  .format(DateTimeFormatter.ofPattern("MMM dd HH:mm"))
                            : "";
                    labels[i].setText(s.getInvoiceNo() + "  —  Rs. "
                            + String.format("%.0f", s.getFinalAmount())
                            + "   " + date);
                    labels[i].setVisible(true);
                    labels[i].setManaged(true);
                } else {
                    labels[i].setVisible(false);
                    labels[i].setManaged(false);
                }
            }
        } catch (Exception e) {
            System.err.println("Dashboard recent sales error: " + e.getMessage());
        }
    }
}
