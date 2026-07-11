package com.possystem.sajilopos.model;

import java.sql.Timestamp;

public class DashboardTransaction {
    private final String invoiceNumber;
    private final String customer;
    private final double amount;
    private final String paymentMethod;
    private final Timestamp date;
    private final String status;

    public DashboardTransaction(String invoiceNumber, String customer, double amount,
                                String paymentMethod, Timestamp date, String status) {
        this.invoiceNumber = invoiceNumber;
        this.customer = customer;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.status = status;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
