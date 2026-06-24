package com.possystem.sajilopos.model;

/**
 * DTO for Product Performance Report
 * Contains detailed metrics for each product's sales and purchase performance
 */
public class ProductPerformanceDTO {
    private String productName;
    private int quantityPurchased;
    private double purchaseAmount;
    private int quantitySold;
    private double salesAmount;
    private double profit;

    public ProductPerformanceDTO() {
        this.productName = "";
        this.quantityPurchased = 0;
        this.purchaseAmount = 0.0;
        this.quantitySold = 0;
        this.salesAmount = 0.0;
        this.profit = 0.0;
    }

    public ProductPerformanceDTO(String productName, int quantityPurchased, double purchaseAmount,
                                int quantitySold, double salesAmount) {
        this.productName = productName;
        this.quantityPurchased = quantityPurchased;
        this.purchaseAmount = purchaseAmount;
        this.quantitySold = quantitySold;
        this.salesAmount = salesAmount;
        this.profit = calculateProfit();
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantityPurchased() {
        return quantityPurchased;
    }

    public void setQuantityPurchased(int quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }

    public double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
        this.profit = calculateProfit();
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(double salesAmount) {
        this.salesAmount = salesAmount;
        this.profit = calculateProfit();
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    private double calculateProfit() {
        return this.salesAmount - this.purchaseAmount;
    }

    @Override
    public String toString() {
        return "ProductPerformanceDTO{" +
                "productName='" + productName + '\'' +
                ", quantityPurchased=" + quantityPurchased +
                ", purchaseAmount=" + purchaseAmount +
                ", quantitySold=" + quantitySold +
                ", salesAmount=" + salesAmount +
                ", profit=" + profit +
                '}';
    }
}
