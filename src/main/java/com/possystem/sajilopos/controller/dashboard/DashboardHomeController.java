package com.possystem.sajilopos.controller.dashboard;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.model.DashboardAggregateModel;
import com.possystem.sajilopos.model.DashboardFooterSummary;

import com.possystem.sajilopos.repository.DashboardRepository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;


import javafx.util.Duration;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardHomeController {

    @FXML private Label todayRevenueLabel;
    @FXML private Label todayRevenueTrendLabel;

    @FXML private Label transactionsTodayLabel;
    @FXML private Label transactionsTrendLabel;
    @FXML private Label productsSoldLabel;
    @FXML private Label productsSoldTrendLabel;

    @FXML private Label lowStockCountLabel;
    @FXML private Label lowStockTrendLabel;

    @FXML private LineChart<String, Number> salesLineChart;

    @FXML private ToggleButton rangeTodayToggle;
    @FXML private ToggleButton rangeWeekToggle;
    @FXML private ToggleButton rangeMonthToggle;




    @FXML private Label totalProductsLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalSuppliersLabel;
    @FXML private Label totalInventoryValueLabel;

    private final DashboardRepository repository = new DashboardRepository();
    private final SessionManager session = SessionManager.getInstance();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("MMM dd");


    private Timeline refreshTimeline;

    private final Service<SummaryPayload> summaryService = new Service<>() {
        @Override
        protected Task<SummaryPayload> createTask() {
            return new Task<>() {
                @Override
                protected SummaryPayload call() {
                    int companyId = session.getCurrentCompanyId();
                    DashboardAggregateModel aggregate = repository.getDashboardSummary(companyId);
                    double todayRevenue = aggregate.getTodayRevenue();
                    double yesterdayRevenue = repository.getYesterdayRevenue(companyId);
                    double growthPercent = 0.0;
                    if (yesterdayRevenue > 0) {
                        growthPercent = ((todayRevenue - yesterdayRevenue) / yesterdayRevenue) * 100.0;
                    }
                    return new SummaryPayload(aggregate, growthPercent, yesterdayRevenue);
                }
            };
        }
    };



    private final Service<DashboardFooterSummary> footerService = new Service<>() {
        @Override
        protected Task<DashboardFooterSummary> createTask() {
            return new Task<>() {
                @Override
                protected DashboardFooterSummary call() {
                    int companyId = session.getCurrentCompanyId();
                    return repository.getFooterSummary(companyId);
                }
            };
        }
    };

    private final Service<AnalyticsPayload> analyticsService = new Service<>() {
        @Override
        protected Task<AnalyticsPayload> createTask() {
            return new Task<>() {
                @Override
                protected AnalyticsPayload call() {
                    int companyId = session.getCurrentCompanyId();
                    DateRange range = getSelectedRange();
                    LocalDate to = LocalDate.now();
                    LocalDate from = range == DateRange.MONTH ? to.minusDays(29)
                            : range == DateRange.WEEK ? to.minusDays(6)
                            : to;
                    Map<LocalDate, Double> salesData = repository.getSalesAnalytics(companyId, from, to);
                    return new AnalyticsPayload(from, to, salesData);
                }
            };
        }
    };

    @FXML
    public void initialize() {
        configureRangeToggles();
        configureServices();
        refreshAllSections();
        startAutoRefresh();
    }


    private void configureRangeToggles() {
        ToggleGroup group = new ToggleGroup();
        rangeTodayToggle.setToggleGroup(group);
        rangeWeekToggle.setToggleGroup(group);
        rangeMonthToggle.setToggleGroup(group);
        rangeWeekToggle.setSelected(true);

        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                restartService(analyticsService);
            }
        });
    }

    private void configureServices() {
        summaryService.setOnSucceeded(event -> updateSummary(summaryService.getValue()));



        footerService.setOnSucceeded(event -> updateFooter(footerService.getValue()));
        analyticsService.setOnSucceeded(event -> updateAnalytics(analyticsService.getValue()));
    }

    private void refreshAllSections() {
        restartService(summaryService);



        restartService(footerService);
        restartService(analyticsService);
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(60), event -> refreshAllSections()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }



    private void updateSummary(SummaryPayload payload) {
        DashboardAggregateModel data = payload.aggregate;

        todayRevenueLabel.setText(formatCurrency(data.getTodayRevenue()));
        transactionsTodayLabel.setText(String.valueOf(data.getTransactionsToday()));
        productsSoldLabel.setText(String.valueOf(data.getProductsSoldToday()));
        lowStockCountLabel.setText(String.valueOf(data.getLowStockCount()));

        todayRevenueTrendLabel.setText(formatRevenueTrend(payload.growthPercent, payload.yesterdayRevenue));
        transactionsTrendLabel.setText("Updated today");
        productsSoldTrendLabel.setText("Updated today");
        lowStockTrendLabel.setText(data.getLowStockCount() > 0 ? "Needs attention" : "Healthy");
    }

    private void updateFooter(DashboardFooterSummary summary) {
        totalProductsLabel.setText(String.valueOf(summary.getTotalProducts()));
        totalCustomersLabel.setText(String.valueOf(summary.getTotalCustomers()));
        totalSuppliersLabel.setText(String.valueOf(summary.getTotalSuppliers()));
        totalInventoryValueLabel.setText(formatCurrency(summary.getTotalInventoryValue()));
    }

    private void updateAnalytics(AnalyticsPayload payload) {
        salesLineChart.getData().clear();
        salesLineChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (LocalDate date : buildDateRange(payload.from, payload.to)) {
            double value = payload.salesData.getOrDefault(date, 0.0);
            series.getData().add(new XYChart.Data<>(shortDateFormatter.format(date), value));
        }
        salesLineChart.getData().add(series);
    }

    private List<LocalDate> buildDateRange(LocalDate from, LocalDate to) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    private DateRange getSelectedRange() {
        if (rangeMonthToggle.isSelected()) {
            return DateRange.MONTH;
        }
        if (rangeTodayToggle.isSelected()) {
            return DateRange.TODAY;
        }
        return DateRange.WEEK;
    }

    private String formatCurrency(double amount) {
        return "Rs. " + currencyFormat.format(amount);
    }

    private String formatRevenueTrend(double growthPercent, double yesterdayRevenue) {
        if (yesterdayRevenue == 0 && growthPercent == 0) {
            return "No sales yesterday";
        }
        String arrow = growthPercent >= 0 ? "▲" : "▼";
        return String.format("%s %.1f%% from yesterday", arrow, Math.abs(growthPercent));
    }

    private void restartService(Service<?> service) {
        service.restart();
    }

    private static class SummaryPayload {
        private final DashboardAggregateModel aggregate;
        private final double growthPercent;
        private final double yesterdayRevenue;

        private SummaryPayload(DashboardAggregateModel aggregate, double growthPercent, double yesterdayRevenue) {
            this.aggregate = aggregate;
            this.growthPercent = growthPercent;
            this.yesterdayRevenue = yesterdayRevenue;
        }
    }

    private static class AnalyticsPayload {
        private final LocalDate from;
        private final LocalDate to;
        private final Map<LocalDate, Double> salesData;
        private AnalyticsPayload(LocalDate from, LocalDate to, Map<LocalDate, Double> salesData) {
            this.from = from;
            this.to = to;
            this.salesData = salesData;
        }
    }

    private enum DateRange {
        TODAY,
        WEEK,
        MONTH
    }

    private class TableCellCurrency<T> extends javafx.scene.control.TableCell<T, Double> {
        @Override
        protected void updateItem(Double value, boolean empty) {
            super.updateItem(value, empty);
            if (empty || value == null) {
                setText(null);
            } else {
                setText(formatCurrency(value));
            }
        }
    }
}
