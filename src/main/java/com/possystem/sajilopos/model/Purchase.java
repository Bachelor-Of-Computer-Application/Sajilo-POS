package com.possystem.sajilopos.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Purchase {
    private int purchaseId;
    private int companyId;
    private int supplierId;
    private String invoiceNo;
    private double totalAmount;
    private Timestamp purchaseDate;
    private List<PurchaseItem> items;

    // Constructor for new purchase (without ID)
    public Purchase(int companyId, int supplierId, String invoiceNo) {
        this.companyId = companyId;
        this.supplierId = supplierId;
        this.invoiceNo = invoiceNo;
        this.totalAmount = 0.0;
        this.items = new ArrayList<>();
    }

    // Constructor for existing purchase (with ID)
    public Purchase(int purchaseId, int companyId, int supplierId, String invoiceNo, 
                   double totalAmount, Timestamp purchaseDate) {
        this.purchaseId = purchaseId;
        this.companyId = companyId;
        this.supplierId = supplierId;
        this.invoiceNo = invoiceNo;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
        this.items = new ArrayList<>();
    }

    // Getters
    public int getPurchaseId() {
        return purchaseId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    // Setters
    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setItems(List<PurchaseItem> items) {
        this.items = items;
    }

    // Helper methods
    public void addItem(PurchaseItem item) {
        this.items.add(item);
        recalculateTotal();
    }

    public void removeItem(PurchaseItem item) {
        this.items.remove(item);
        recalculateTotal();
    }

    public void removeItemAt(int index) {
        if (index >= 0 && index < this.items.size()) {
            this.items.remove(index);
            recalculateTotal();
        }
    }

    public void recalculateTotal() {
        this.totalAmount = this.items.stream()
                .mapToDouble(PurchaseItem::getTotal)
                .sum();
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "purchaseId=" + purchaseId +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", totalAmount=" + totalAmount +
                ", items=" + items.size() +
                '}';
    }
}
