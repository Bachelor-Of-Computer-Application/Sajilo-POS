package com.possystem.sajilopos.model;

public class DashboardLowStockItem {
    private final String productName;
    private final int currentStock;
    private final int reorderLevel;

    public DashboardLowStockItem(String productName, int currentStock, int reorderLevel) {
        this.productName = productName;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
    }

    public String getProductName() {
        return productName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }
}
