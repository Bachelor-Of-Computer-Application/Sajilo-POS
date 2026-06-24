package com.possystem.sajilopos.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int saleId;
    private int companyId;
    private int customerId;
    private String invoiceNo;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private String paymentMethod;
    private double amountPaid;
    private double changeAmount;
    private int soldBy;
    private Timestamp saleDate;
    private List<SaleItem> items;

    // Constructor for new sale (without ID)
    public Sale(int companyId, int customerId, String invoiceNo, int soldBy) {
        this.companyId = companyId;
        this.customerId = customerId;
        this.invoiceNo = invoiceNo;
        this.soldBy = soldBy;
        this.totalAmount = 0.0;
        this.discountAmount = 0.0;
        this.finalAmount = 0.0;
        this.amountPaid = 0.0;
        this.changeAmount = 0.0;
        this.items = new ArrayList<>();
    }

    // Constructor for existing sale (with ID)
    public Sale(int saleId, int companyId, int customerId, String invoiceNo, double totalAmount,
               double discountAmount, double finalAmount, String paymentMethod, double amountPaid,
               double changeAmount, int soldBy, Timestamp saleDate) {
        this.saleId = saleId;
        this.companyId = companyId;
        this.customerId = customerId;
        this.invoiceNo = invoiceNo;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeAmount = changeAmount;
        this.soldBy = soldBy;
        this.saleDate = saleDate;
        this.items = new ArrayList<>();
    }

    // Getters
    public int getSaleId() { return saleId; }
    public int getCompanyId() { return companyId; }
    public int getCustomerId() { return customerId; }
    public String getInvoiceNo() { return invoiceNo; }
    public double getTotalAmount() { return totalAmount; }
    public double getDiscountAmount() { return discountAmount; }
    public double getFinalAmount() { return finalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getAmountPaid() { return amountPaid; }
    public double getChangeAmount() { return changeAmount; }
    public int getSoldBy() { return soldBy; }
    public Timestamp getSaleDate() { return saleDate; }
    public List<SaleItem> getItems() { return items; }

    // Setters
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }
    public void setSoldBy(int soldBy) { this.soldBy = soldBy; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }
    public void setItems(List<SaleItem> items) { this.items = items; }

    // Helper methods
    public void addItem(SaleItem item) {
        this.items.add(item);
        recalculateTotals();
    }

    public void removeItem(SaleItem item) {
        this.items.remove(item);
        recalculateTotals();
    }

    public void removeItemAt(int index) {
        if (index >= 0 && index < this.items.size()) {
            this.items.remove(index);
            recalculateTotals();
        }
    }

    public void recalculateTotals() {
        this.totalAmount = this.items.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();
        this.finalAmount = this.totalAmount - this.discountAmount;
    }

    public void calculateChange() {
        this.changeAmount = this.amountPaid - this.finalAmount;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", finalAmount=" + finalAmount +
                ", items=" + items.size() +
                '}';
    }
}
