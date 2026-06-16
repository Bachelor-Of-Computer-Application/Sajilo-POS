package com.possystem.sajilopos.model;

public class PurchaseItem {
    private int purchaseItemId;
    private int purchaseId;
    private int productId;
    private String productName; // For display
    private double purchasePrice;
    private int quantity;
    private double total;

    // Constructor for new purchase item (without ID and purchase ID)
    public PurchaseItem(int productId, String productName, double purchasePrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.total = purchasePrice * quantity;
    }

    // Constructor for existing purchase item (with all fields)
    public PurchaseItem(int purchaseItemId, int purchaseId, int productId, String productName,
                       double purchasePrice, int quantity, double total) {
        this.purchaseItemId = purchaseItemId;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.productName = productName;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.total = total;
    }

    // Getters
    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }

    // Setters
    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
        recalculateTotal();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateTotal();
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Helper method to recalculate total
    private void recalculateTotal() {
        this.total = this.purchasePrice * this.quantity;
    }

    @Override
    public String toString() {
        return "PurchaseItem{" +
                "productName='" + productName + '\'' +
                ", purchasePrice=" + purchasePrice +
                ", quantity=" + quantity +
                ", total=" + total +
                '}';
    }
}
