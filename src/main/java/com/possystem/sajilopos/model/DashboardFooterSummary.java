package com.possystem.sajilopos.model;

public class DashboardFooterSummary {
    private final int totalProducts;
    private final int totalCustomers;
    private final int totalSuppliers;
    private final double totalInventoryValue;

    public DashboardFooterSummary(int totalProducts, int totalCustomers, int totalSuppliers,
                                  double totalInventoryValue) {
        this.totalProducts = totalProducts;
        this.totalCustomers = totalCustomers;
        this.totalSuppliers = totalSuppliers;
        this.totalInventoryValue = totalInventoryValue;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public int getTotalSuppliers() {
        return totalSuppliers;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }
}
