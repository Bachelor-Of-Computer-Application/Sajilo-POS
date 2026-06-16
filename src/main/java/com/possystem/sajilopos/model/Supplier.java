package com.possystem.sajilopos.model;

import java.sql.Timestamp;

public class Supplier {
    private int supplierId;
    private int companyId;
    private String supplierName;
    private String phone;
    private String address;
    private Timestamp createdAt;

    // Constructor for creating new supplier (without ID)
    public Supplier(int companyId, String supplierName, String phone, String address) {
        this.companyId = companyId;
        this.supplierName = supplierName;
        this.phone = phone;
        this.address = address;
    }

    // Constructor for existing supplier (with ID)
    public Supplier(int supplierId, int companyId, String supplierName, String phone, 
                   String address, Timestamp createdAt) {
        this.supplierId = supplierId;
        this.companyId = companyId;
        this.supplierName = supplierName;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    // Getters
    public int getSupplierId() {
        return supplierId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return supplierName; // For ComboBox display
    }
}
