package com.possystem.sajilopos.service;

import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;

import java.time.format.DateTimeFormatter;

public class InvoiceService {

    private static final String STORE_NAME = "Sajilo POS";
    private static final String SEPARATOR = "==========================================";
    private static final String LINE = "------------------------------------------";

    // Generate invoice as plain text string
    public String generateInvoice(Sale sale, String cashierName) {
        StringBuilder invoice = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        invoice.append(SEPARATOR).append("\n");
        invoice.append("         ").append(STORE_NAME).append("\n");
        invoice.append(SEPARATOR).append("\n");
        invoice.append(String.format("Date     : %s%n", sale.getSaleDate().format(formatter)));
        invoice.append(String.format("Cashier  : %s%n", cashierName));
        invoice.append(String.format("Sale ID  : #%d%n", sale.getId()));
        invoice.append(LINE).append("\n");
        invoice.append(String.format("%-20s %5s %10s%n", "Item", "Qty", "Subtotal"));
        invoice.append(LINE).append("\n");

        for (SaleItem item : sale.getItems()) {
            invoice.append(String.format("%-20s %5d %10.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getSubtotal()));
        }

        invoice.append(LINE).append("\n");
        invoice.append(String.format("%-26s %10.2f%n", "Total:", sale.getTotalAmount()));

        if (sale.getDiscount() > 0) {
            invoice.append(String.format("%-26s %10.2f%n", "Discount:", sale.getDiscount()));
        }

        invoice.append(String.format("%-26s %10.2f%n", "Final Amount:", sale.getFinalAmount()));
        invoice.append(SEPARATOR).append("\n");
        invoice.append("         Thank you for shopping!\n");
        invoice.append(SEPARATOR).append("\n");

        return invoice.toString();
    }

    // Print invoice to console (UI person can redirect this to a printer)
    public void printInvoice(Sale sale, String cashierName) {
        System.out.println(generateInvoice(sale, cashierName));
    }
}
