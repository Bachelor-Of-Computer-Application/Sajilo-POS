package com.possystem.sajilopos.controller.reports;

import com.possystem.sajilopos.service.ReportService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Map;

public class reportscontroller {

    @FXML private TextField searchField;
    @FXML private TableView<ReportRow> reportsTable;

    @FXML private TableColumn<ReportRow, String> colDate;
    @FXML private TableColumn<ReportRow, Double> colSales;
    @FXML private TableColumn<ReportRow, Integer> colItems;
    @FXML private TableColumn<ReportRow, Double> colProfit;

    private final ObservableList<ReportRow> reportList = FXCollections.observableArrayList();
    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colSales.setCellValueFactory(data -> data.getValue().salesProperty().asObject());
        colItems.setCellValueFactory(data -> data.getValue().itemsProperty().asObject());
        colProfit.setCellValueFactory(data -> data.getValue().profitProperty().asObject());

        reportsTable.setItems(reportList);
        loadLast30Days();
    }

    private void loadLast30Days() {
        try {
            LocalDate to = LocalDate.now();
            LocalDate from = to.minusDays(29);
            loadRange(from, to);
        } catch (Exception e) {
            System.err.println("Error loading report data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRange(LocalDate from, LocalDate to) {
        reportList.clear();

        try {
            Map<String, Double> dailySales =
                    reportService.getDailySalesSummary(from, to);

            int totalTransactions =
                    reportService.getTotalTransactions(from, to);

            int days = dailySales.size() == 0 ? 1 : dailySales.size();

            for (Map.Entry<String, Double> entry : dailySales.entrySet()) {

                double sales = entry.getValue();

                // Estimate items per day proportionally from total transactions
                int items = totalTransactions / days;

                // Estimated 20% margin — no cost price in DB yet
                double profit = sales * 0.20;

                reportList.add(new ReportRow(
                        entry.getKey(),
                        sales,
                        items,
                        profit
                ));
            }

            // If no daily data, show a summary row
            if (reportList.isEmpty()) {

                double totalSales =
                        reportService.getTotalSales(from, to);

                if (totalSales > 0) {

                    reportList.add(new ReportRow(
                            from + " to " + to,
                            totalSales,
                            totalTransactions,
                            totalSales * 0.20 // Estimated 20% margin
                    ));
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading range report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onFilter() {

        String keyword = searchField.getText()
                .trim()
                .toLowerCase();

        if (keyword.isEmpty()) {
            loadLast30Days();
            return;
        }

        ObservableList<ReportRow> filtered =
                FXCollections.observableArrayList();

        for (ReportRow row : reportList) {
            if (row.getDate().toLowerCase().contains(keyword)) {
                filtered.add(row);
            }
        }

        reportsTable.setItems(filtered);
    }

    @FXML
    private void onRefresh() {
        searchField.clear();
        reportsTable.setItems(reportList);
        loadLast30Days();
    }

    @FXML
    private void onExport() {

        try {

            // Can be extended later to CSV/PDF export
            reportService.printMonthlyReport();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export");
            alert.setHeaderText(null);
            alert.setContentText("Report summary printed to console.");
            alert.showAndWait();

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Report Row Model
    // ─────────────────────────────────────────────────────────────

    public static class ReportRow {

        private final SimpleStringProperty date;
        private final SimpleDoubleProperty sales;
        private final SimpleIntegerProperty items;
        private final SimpleDoubleProperty profit;

        public ReportRow(String date,
                         double sales,
                         int items,
                         double profit) {

            this.date = new SimpleStringProperty(date);
            this.sales = new SimpleDoubleProperty(sales);
            this.items = new SimpleIntegerProperty(items);
            this.profit = new SimpleDoubleProperty(profit);
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public SimpleDoubleProperty salesProperty() {
            return sales;
        }

        public SimpleIntegerProperty itemsProperty() {
            return items;
        }

        public SimpleDoubleProperty profitProperty() {
            return profit;
        }

        public String getDate() {
            return date.get();
        }
    }
}