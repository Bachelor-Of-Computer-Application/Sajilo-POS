package com.possystem.sajilopos.model;

public class SaleItem {
    private Product product;
    private int quantity;
    private double subtotal;

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
}