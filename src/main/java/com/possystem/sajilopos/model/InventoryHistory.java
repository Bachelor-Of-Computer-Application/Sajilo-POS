package com.possystem.sajilopos.model;

import java.sql.Timestamp;

public class InventoryHistory {
    private int historyId;
    private int companyId;
    private int productId;
    private String productName; // For display purposes
    private ActionType actionType;
    private int quantity;
    private String remarks;
    private Timestamp createdAt;

    public enum ActionType {
        PURCHASE, SALE, ADJUSTMENT
    }

    // Constructor for creating new history (without ID)
    public InventoryHistory(int companyId, int productId, ActionType actionType, int quantity, String remarks) {
        this.companyId = companyId;
        this.productId = productId;
        this.actionType = actionType;
        this.quantity = quantity;
        this.remarks = remarks;
    }

    // Constructor for existing history (with ID)
    public InventoryHistory(int historyId, int companyId, int productId, String productName,
                           ActionType actionType, int quantity, String remarks, Timestamp createdAt) {
        this.historyId = historyId;
        this.companyId = companyId;
        this.productId = productId;
        this.productName = productName;
        this.actionType = actionType;
        this.quantity = quantity;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    // Getters
    public int getHistoryId() {
        return historyId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InventoryHistory{" +
                "historyId=" + historyId +
                ", companyId=" + companyId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", actionType=" + actionType +
                ", quantity=" + quantity +
                ", remarks='" + remarks + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
