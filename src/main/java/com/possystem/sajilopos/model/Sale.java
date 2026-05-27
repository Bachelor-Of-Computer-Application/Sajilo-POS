package com.possystem.sajilopos.model;

import java.time.LocalDateTime;
import java.util.List;


public class Sale {
    private int id;
    private int userId;
    private List<SaleItem> items;
    private double totalAmount;
    private double discount;
    private double finalAmount;
    private LocalDateTime saleDate;

    
    public Sale(int id, List<SaleItem> items, double discount, int userId) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.discount = discount;
        this.saleDate = LocalDateTime.now();
        this.totalAmount = items.stream().mapToDouble(SaleItem::getSubtotal).sum();
        this.finalAmount = totalAmount - discount;
    }

   
    public Sale(int id, List<SaleItem> items, double discount) {
        this(id, items, discount, 0);
    }


    public int getId() {
        return id;
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
}
