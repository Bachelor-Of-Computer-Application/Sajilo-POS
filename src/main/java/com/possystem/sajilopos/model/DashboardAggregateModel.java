package com.possystem.sajilopos.model;

public class DashboardAggregateModel {
    private double todayRevenue;
    private double todayProfit;
    private int transactionsToday;
    private int productsSoldToday;
    private int activeCustomers;
    private int lowStockCount;
    private double inventoryValue;

    public DashboardAggregateModel() {
    }

    public DashboardAggregateModel(double todayRevenue, double todayProfit, int transactionsToday,
                                   int productsSoldToday, int activeCustomers, int lowStockCount,
                                   double inventoryValue) {
        this.todayRevenue = todayRevenue;
        this.todayProfit = todayProfit;
        this.transactionsToday = transactionsToday;
        this.productsSoldToday = productsSoldToday;
        this.activeCustomers = activeCustomers;
        this.lowStockCount = lowStockCount;
        this.inventoryValue = inventoryValue;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public double getTodayProfit() {
        return todayProfit;
    }

    public void setTodayProfit(double todayProfit) {
        this.todayProfit = todayProfit;
    }

    public int getTransactionsToday() {
        return transactionsToday;
    }

    public void setTransactionsToday(int transactionsToday) {
        this.transactionsToday = transactionsToday;
    }

    public int getProductsSoldToday() {
        return productsSoldToday;
    }

    public void setProductsSoldToday(int productsSoldToday) {
        this.productsSoldToday = productsSoldToday;
    }

    public int getActiveCustomers() {
        return activeCustomers;
    }

    public void setActiveCustomers(int activeCustomers) {
        this.activeCustomers = activeCustomers;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public double getInventoryValue() {
        return inventoryValue;
    }

    public void setInventoryValue(double inventoryValue) {
        this.inventoryValue = inventoryValue;
    }
}
