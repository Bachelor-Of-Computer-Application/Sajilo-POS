package com.possystem.sajilopos.controller.reports;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class reportscontroller {

    @FXML private TextField searchField;
    @FXML private TableView<Report> reportsTable;

    @FXML private TableColumn<Report, String> colDate;
    @FXML private TableColumn<Report, Double> colSales;
    @FXML private TableColumn<Report, Integer> colItems;
    @FXML private TableColumn<Report, Double> colProfit;

    private final ObservableList<Report> reportList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colSales.setCellValueFactory(data -> data.getValue().salesProperty().asObject());
        colItems.setCellValueFactory(data -> data.getValue().itemsProperty().asObject());
        colProfit.setCellValueFactory(data -> data.getValue().profitProperty().asObject());

        loadMockData();
        reportsTable.setItems(reportList);
    }

    private void loadMockData() {
        reportList.addAll(
                new Report("2026-06-01", 50000, 120, 12000),
                new Report("2026-06-02", 42000, 98, 9000),
                new Report("2026-06-03", 61000, 140, 15000),
                new Report("2026-06-04", 39000, 85, 8000)
        );
    }

    @FXML
    private void onFilter() {
        String keyword = searchField.getText().toLowerCase();

        if (keyword.isEmpty()) {
            reportsTable.setItems(reportList);
            return;
        }

        ObservableList<Report> filtered = FXCollections.observableArrayList(
                reportList.stream()
                        .filter(r -> r.getDate().toLowerCase().contains(keyword))
                        .collect(Collectors.toList())
        );

        reportsTable.setItems(filtered);
    }

    @FXML
    private void onRefresh() {
        reportsTable.setItems(reportList);
    }

    @FXML
    private void onExport() {
        System.out.println("Exporting reports...");
    }

    public static class Report {
        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleDoubleProperty sales;
        private final javafx.beans.property.SimpleIntegerProperty items;
        private final javafx.beans.property.SimpleDoubleProperty profit;

        public Report(String date, double sales, int items, double profit) {
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.sales = new javafx.beans.property.SimpleDoubleProperty(sales);
            this.items = new javafx.beans.property.SimpleIntegerProperty(items);
            this.profit = new javafx.beans.property.SimpleDoubleProperty(profit);
        }

        public javafx.beans.property.SimpleStringProperty dateProperty() { return date; }
        public javafx.beans.property.SimpleDoubleProperty salesProperty() { return sales; }
        public javafx.beans.property.SimpleIntegerProperty itemsProperty() { return items; }
        public javafx.beans.property.SimpleDoubleProperty profitProperty() { return profit; }

        public String getDate() { return date.get(); }
    }
}