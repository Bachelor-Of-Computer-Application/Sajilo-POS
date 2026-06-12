package com.possystem.sajilopos.model;

import java.time.LocalDateTime;

/**
 * Company Model
 * Represents a company in the system
 */
public class Company {
    private int companyId;
    private String companyName;
    private String companyCode;
    private LocalDateTime createdAt;

    // Constructor with all fields
    public Company(int companyId, String companyName, String companyCode, LocalDateTime createdAt) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.createdAt = createdAt;
    }

    // Constructor without timestamp
    public Company(int companyId, String companyName, String companyCode) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyCode = companyCode;
    }

    // Getters and Setters
    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", companyCode='" + companyCode + '\'' +
                '}';
    }
}
