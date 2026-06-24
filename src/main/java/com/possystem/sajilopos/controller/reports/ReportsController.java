package com.possystem.sajilopos.controller.reports;

import com.possystem.sajilopos.dao.ProductReportDAO;
import com.possystem.sajilopos.dao.ReportDAO;
import com.possystem.sajilopos.model.ProductPerformanceDTO;
import com.possystem.sajilopos.model.SummaryReportDTO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.print.PrinterJob;
import javafx.scene.transform.Scale;

import java.time.LocalDate;

public class ReportsController {

    // ==================== FXML Components ====================

    // Period Selection
    @FXML
    private RadioButton radioDailyPeriod;
    @FXML
    private RadioButton radioWeeklyPeriod;
    @FXML
    private RadioButton radioMonthlyPeriod;
    @FXML
    private RadioButton radioYearlyPeriod;
    private ToggleGroup periodGroup;

    // Date Picker
    @FXML
    private DatePicker datePicker;

    // Generate Button
    @FXML
    private Button generateButton;

    // Summary Report Section
    @FXML
    private Label lblTotalSales;
    @FXML
    private Label lblTotalPurchases;
    @FXML
    private Label lblProfit;
    @FXML
    private Label lblTotalTransactions;
    @FXML
    private Label lblProductsInfo;

    // Product Performance Table
    @FXML
    private TableView<ProductPerformanceRow> productTable;
    @FXML
    private TableColumn<ProductPerformanceRow, String> colProductName;
    @FXML
    private TableColumn<ProductPerformanceRow, Integer> colQtyPurchased;
    @FXML
    private TableColumn<ProductPerformanceRow, Double> colPurchaseAmount;
    @FXML
    private TableColumn<ProductPerformanceRow, Integer> colQtySold;
    @FXML
    private TableColumn<ProductPerformanceRow, Double> colSalesAmount;
    @FXML
    private TableColumn<ProductPerformanceRow, Double> colProfit;

    // Total Profit Label
    @FXML
    private Label lblTotalProductProfit;

    // Action Buttons
    @FXML
    private Button printButton;
    @FXML
    private Button refreshButton;

    // Content Area for printing
    @FXML
    private VBox reportContent;

    // ==================== Private Fields ====================
    private final ReportDAO reportDAO = new ReportDAO();
    private final ProductReportDAO productReportDAO = new ProductReportDAO();
    private final ObservableList<ProductPerformanceRow> productList = FXCollections.observableArrayList();
    private SummaryReportDTO currentSummary;
    private LocalDate currentFromDate;
    private LocalDate currentToDate;

    // ==================== Initialization ====================

    @FXML
    public void initialize() {
        setupPeriodRadioButtons();
        setupTableColumns();
        productTable.setItems(productList);

        datePicker.setValue(LocalDate.now());
        radioDailyPeriod.setSelected(true);
    }

    private void setupPeriodRadioButtons() {
        periodGroup = new ToggleGroup();
        radioDailyPeriod.setToggleGroup(periodGroup);
        radioWeeklyPeriod.setToggleGroup(periodGroup);
        radioMonthlyPeriod.setToggleGroup(periodGroup);
        radioYearlyPeriod.setToggleGroup(periodGroup);
    }

    private void setupTableColumns() {
        colProductName.setCellValueFactory(data -> data.getValue().productNameProperty());
        colQtyPurchased.setCellValueFactory(data -> data.getValue().qtyPurchasedProperty().asObject());
        colPurchaseAmount.setCellValueFactory(data -> data.getValue().purchaseAmountProperty().asObject());
        colQtySold.setCellValueFactory(data -> data.getValue().qtySoldProperty().asObject());
        colSalesAmount.setCellValueFactory(data -> data.getValue().salesAmountProperty().asObject());
        colProfit.setCellValueFactory(data -> data.getValue().profitProperty().asObject());

        // Format currency columns
        colPurchaseAmount.setCellFactory(column -> createCurrencyCell());
        colSalesAmount.setCellFactory(column -> createCurrencyCell());
        colProfit.setCellFactory(column -> createCurrencyCell());
    }

    private TableCell<ProductPerformanceRow, Double> createCurrencyCell() {
        return new TableCell<ProductPerformanceRow, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rs. %.2f", item));
                }
            }
        };
    }

    // ==================== Event Handlers ====================

    @FXML
    private void onGenerateReport() {
        try {
            calculateDateRange();
            loadSummaryReport();
            loadProductPerformanceReport();
            updateUI();
        } catch (Exception e) {
            showError("Error generating report", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onPrintReport() {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(reportContent.getScene().getWindow())) {
                // Scale the content to fit the page
                double scaleX = job.getJobSettings().getPageLayout().getPrintableWidth() / reportContent.getWidth();
                double scaleY = job.getJobSettings().getPageLayout().getPrintableHeight() / reportContent.getHeight();
                double scale = Math.min(scaleX, scaleY);

                Scale scaleTransform = new Scale(scale, scale);
                reportContent.getTransforms().add(scaleTransform);

                boolean printed = job.printPage(reportContent);

                reportContent.getTransforms().clear();

                if (printed) {
                    job.endJob();
                    showInfo("Print", "Report printed successfully.");
                }
            }
        } catch (Exception e) {
            showError("Error printing report", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onRefresh() {
        onGenerateReport();
    }

    // ==================== Report Loading Methods ====================

    private void calculateDateRange() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        if (radioDailyPeriod.isSelected()) {
            currentFromDate = selectedDate;
            currentToDate = selectedDate;
        } else if (radioWeeklyPeriod.isSelected()) {
            currentFromDate = selectedDate.minusDays(6); // 7 days including today
            currentToDate = selectedDate;
        } else if (radioMonthlyPeriod.isSelected()) {
            currentFromDate = selectedDate.withDayOfMonth(1);
            currentToDate = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());
        } else if (radioYearlyPeriod.isSelected()) {
            currentFromDate = selectedDate.withDayOfYear(1);
            currentToDate = selectedDate.withDayOfYear(selectedDate.lengthOfYear());
        }
    }

    private void loadSummaryReport() {
        try {
            currentSummary = reportDAO.getSummaryReport(currentFromDate, currentToDate);
            if (currentSummary == null) {
                currentSummary = new SummaryReportDTO();
            }
        } catch (Exception e) {
            showError("Error loading summary report", e.getMessage());
            currentSummary = new SummaryReportDTO();
        }
    }

    private void loadProductPerformanceReport() {
        productList.clear();
        try {
            var products = productReportDAO.getProductPerformanceReport(currentFromDate, currentToDate);
            for (ProductPerformanceDTO dto : products) {
                productList.add(new ProductPerformanceRow(
                        dto.getProductName(),
                        dto.getQuantityPurchased(),
                        dto.getPurchaseAmount(),
                        dto.getQuantitySold(),
                        dto.getSalesAmount()));
            }
        } catch (Exception e) {
            showError("Error loading product performance report", e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== UI Update Methods ====================

    private void updateUI() {
        // Update Summary Section
        lblTotalSales.setText(String.format("Rs. %.2f", currentSummary.getTotalSales()));
        lblTotalPurchases.setText(String.format("Rs. %.2f", currentSummary.getTotalPurchases()));
        lblProfit.setText(String.format("Rs. %.2f", currentSummary.getProfit()));
        lblTotalTransactions.setText(String.valueOf(currentSummary.getTotalTransactions()));
        lblProductsInfo.setText(String.format("Purchased: %d | Sold: %d",
                currentSummary.getTotalProductsPurchased(),
                currentSummary.getTotalProductsSold()));

        // Update Product Performance Section
        double totalProductProfit = productReportDAO.getTotalProductProfit(currentFromDate, currentToDate);
        lblTotalProductProfit.setText(String.format("Rs. %.2f", totalProductProfit));

        // Update period label
        String periodLabel = String.format("%s to %s", currentFromDate, currentToDate);
        // You can add a label to show the current period if needed
    }

    // ==================== Helper Methods ====================

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== Inner Class: ProductPerformanceRow ====================

    public static class ProductPerformanceRow {
        private final SimpleStringProperty productName;
        private final SimpleIntegerProperty qtyPurchased;
        private final SimpleDoubleProperty purchaseAmount;
        private final SimpleIntegerProperty qtySold;
        private final SimpleDoubleProperty salesAmount;
        private final SimpleDoubleProperty profit;

        public ProductPerformanceRow(String productName, int qtyPurchased, double purchaseAmount,
                int qtySold, double salesAmount) {
            this.productName = new SimpleStringProperty(productName);
            this.qtyPurchased = new SimpleIntegerProperty(qtyPurchased);
            this.purchaseAmount = new SimpleDoubleProperty(purchaseAmount);
            this.qtySold = new SimpleIntegerProperty(qtySold);
            this.salesAmount = new SimpleDoubleProperty(salesAmount);
            this.profit = new SimpleDoubleProperty(salesAmount - purchaseAmount);
        }

        // Properties
        public SimpleStringProperty productNameProperty() {
            return productName;
        }

        public SimpleIntegerProperty qtyPurchasedProperty() {
            return qtyPurchased;
        }

        public SimpleDoubleProperty purchaseAmountProperty() {
            return purchaseAmount;
        }

        public SimpleIntegerProperty qtySoldProperty() {
            return qtySold;
        }

        public SimpleDoubleProperty salesAmountProperty() {
            return salesAmount;
        }

        public SimpleDoubleProperty profitProperty() {
            return profit;
        }

        // Getters
        public String getProductName() {
            return productName.get();
        }

        public int getQtyPurchased() {
            return qtyPurchased.get();
        }

        public double getPurchaseAmount() {
            return purchaseAmount.get();
        }

        public int getQtySold() {
            return qtySold.get();
        }

        public double getSalesAmount() {
            return salesAmount.get();
        }

        public double getProfit() {
            return profit.get();
        }
    }
}
