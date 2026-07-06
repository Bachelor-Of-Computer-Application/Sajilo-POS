package com.possystem.sajilopos.model;

/**
 * DTO for Summary Report
 * Contains aggregated financial metrics for a given period
 */
public class SummaryReportDTO {
    private double totalSales;
    private double totalPurchases;
    private double profit;
    private int totalTransactions;
    private int totalProductsPurchased;
    private int totalProductsSold;

    public SummaryReportDTO() {
        this.totalSales = 0.0;
        this.totalPurchases = 0.0;
        this.profit = 0.0;
        this.totalTransactions = 0;
        this.totalProductsPurchased = 0;
        this.totalProductsSold = 0;
    }

    public SummaryReportDTO(double totalSales, double totalPurchases, int totalTransactions,
                           int totalProductsPurchased, int totalProductsSold) {
        this.totalSales = totalSales;
        this.totalPurchases = totalPurchases;
        this.profit = totalSales - totalPurchases;
        this.totalTransactions = totalTransactions;
        this.totalProductsPurchased = totalProductsPurchased;
        this.totalProductsSold = totalProductsSold;
    }

    // Getters and Setters
    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
        calculateProfit();
    }

    public double getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(double totalPurchases) {
        this.totalPurchases = totalPurchases;
        calculateProfit();
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getTotalProductsPurchased() {
        return totalProductsPurchased;
    }

    public void setTotalProductsPurchased(int totalProductsPurchased) {
        this.totalProductsPurchased = totalProductsPurchased;
    }

    public int getTotalProductsSold() {
        return totalProductsSold;
    }

    public void setTotalProductsSold(int totalProductsSold) {
        this.totalProductsSold = totalProductsSold;
    }

    private void calculateProfit() {
        this.profit = this.totalSales - this.totalPurchases;
    }

    @Override
    public String toString() {
        return "SummaryReportDTO{" +
                "totalSales=" + totalSales +
                ", totalPurchases=" + totalPurchases +
                ", profit=" + profit +
                ", totalTransactions=" + totalTransactions +
                ", totalProductsPurchased=" + totalProductsPurchased +
                ", totalProductsSold=" + totalProductsSold +
                '}';
    }
}
