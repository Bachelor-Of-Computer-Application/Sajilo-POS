package com.possystem.sajilopos.model;

public class DashboardTopProduct {
    private final String productName;
    private final int quantitySold;
    private final double revenueGenerated;

    public DashboardTopProduct(String productName, int quantitySold, double revenueGenerated) {
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.revenueGenerated = revenueGenerated;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getRevenueGenerated() {
        return revenueGenerated;
    }
}
