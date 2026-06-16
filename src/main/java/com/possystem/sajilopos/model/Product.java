package com.possystem.sajilopos.model;

import java.sql.Timestamp;

public class Product {
    private int productId;
    private int companyId;
    private String productName;
    private double price;
    private int stock;
    private String description;
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int minimumStock;

    // Constructor for creating new products (without ID)
    public Product(int companyId, String productName, double price, int stock, String description, int minimumStock) {
        this.companyId = companyId;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.active = true;
        this.minimumStock = minimumStock;
    }

    // Constructor for existing products (with ID)
    public Product(int productId, int companyId, String productName, double price, int stock, 
                   String description, boolean active, Timestamp createdAt, Timestamp updatedAt, int minimumStock) {
        this.productId = productId;
        this.companyId = companyId;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.minimumStock = minimumStock;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    /**
     * Get stock status based on minimum stock threshold
     */
    public String getStockStatus() {
        if (stock <= 0) {
            return "Out of Stock";
        } else if (stock <= minimumStock) {
            return "Low";
        } else {
            return "Normal";
        }
    }

    /**
     * Check if product is low in stock
     */
    public boolean isLowStock() {
        return stock > 0 && stock <= minimumStock;
    }

    /**
     * Check if product is out of stock
     */
    public boolean isOutOfStock() {
        return stock <= 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", companyId=" + companyId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", active=" + active +
                ", minimumStock=" + minimumStock +
                '}';
    }
}