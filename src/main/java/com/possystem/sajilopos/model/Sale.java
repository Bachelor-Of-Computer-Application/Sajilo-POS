package com.possystem.sajilopos.model;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private int id;
    private List<SaleItem> items;
    private double totalAmount;
    private double discount;
    private double finalAmount;
    private LocalDateTime saleDate;

    public Sale(int id, List<SaleItem> items, double discount) {
        this.id = id;
        this.items = items;
        this.discount = discount;
        this.saleDate = LocalDateTime.now();
        this.totalAmount = items.stream().mapToDouble(SaleItem::getSubtotal).sum();
        this.finalAmount = totalAmount - discount;
    }

    public int getId() { return id; }
    public List<SaleItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public double getDiscount() { return discount; }
    public double getFinalAmount() { return finalAmount; }
    public LocalDateTime getSaleDate() { return saleDate; }
}
