package com.possystem.sajilopos.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Sale Model
 * Represents a sales transaction
 */
public class Sale {
    private int id;
    private int companyId;
    private int userId;
    private List<SaleItem> items;
    private double totalAmount;
    private double discount;
    private double finalAmount;
    private LocalDateTime saleDate;

    // Main constructor with all fields including id and companyId
    public Sale(int id, int companyId, List<SaleItem> items, double discount, int userId) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.items = items;
        this.discount = discount;
        this.saleDate = LocalDateTime.now();
        this.totalAmount = items.stream().mapToDouble(SaleItem::getSubtotal).sum();
        this.finalAmount = totalAmount - discount;
    }

    // Constructor without ID (for new sales) - includes companyId
    public Sale(int companyId, List<SaleItem> items, double discount, int userId) {
        this.id = 0;
        this.companyId = companyId;
        this.userId = userId;
        this.items = items;
        this.discount = discount;
        this.saleDate = LocalDateTime.now();
        this.totalAmount = items.stream().mapToDouble(SaleItem::getSubtotal).sum();
        this.finalAmount = totalAmount - discount;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public int getUserId() {
        return userId;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }
}
