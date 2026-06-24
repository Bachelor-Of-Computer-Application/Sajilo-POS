package com.possystem.sajilopos.model;

public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    // Constructor for new sale item (without ID)
    public SaleItem(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    // Constructor for existing sale item (with ID)
    public SaleItem(int saleItemId, int saleId, int productId, String productName,
                   int quantity, double unitPrice, double subtotal) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // Getters
    public int getSaleItemId() { return saleItemId; }
    public int getSaleId() { return saleId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return subtotal; }

    // Setters
    public void setSaleItemId(int saleItemId) { this.saleItemId = saleItemId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    // Helper methods
    private void recalculateSubtotal() {
        this.subtotal = this.quantity * this.unitPrice;
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}
