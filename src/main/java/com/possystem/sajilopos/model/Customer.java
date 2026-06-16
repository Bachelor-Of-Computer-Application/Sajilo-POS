package com.possystem.sajilopos.model;

import java.sql.Timestamp;

public class Customer {
    private int customerId;
    private String customerName;
    private String phone;
    private String address;
    private int loyaltyPoints;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor for creating new customers (without ID)
    public Customer(String customerName, String phone, String address, int loyaltyPoints) {
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
    }

    // Constructor for existing customers (with ID)
    public Customer(int customerId, String customerName, String phone, String address, 
                   int loyaltyPoints, Timestamp createdAt, Timestamp updatedAt) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                '}';
    }
}
