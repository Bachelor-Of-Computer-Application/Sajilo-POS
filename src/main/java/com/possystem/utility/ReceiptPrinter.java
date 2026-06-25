package com.possystem.sajilopos.util;

import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class ReceiptPrinter {

    public static void printReceipt(Sale sale, String cashierName) {
        String receipt = generateReceiptText(sale, cashierName);

        TextArea textArea = new TextArea(receipt);
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(500);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Receipt");
        alert.setHeaderText("Invoice: " + sale.getInvoiceNo());
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    public static String generateReceiptText(Sale sale, String cashierName) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           SAJILO POS RECEIPT           \n");
        sb.append("========================================\n");
        sb.append(String.format("Invoice : %s%n", sale.getInvoiceNo()));
        sb.append(String.format("Cashier : %s%n", cashierName));
        sb.append(String.format("Date    : %s%n", sale.getSaleDate()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s%n", "Item", "Qty", "Subtotal"));
        sb.append("----------------------------------------\n");

        for (SaleItem item : sale.getItems()) {
            sb.append(String.format("%-20s %5d %10.2f%n",
                item.getProductName(),
                item.getQuantity(),
                item.getSubtotal()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%-26s %10.2f%n", "Subtotal:", sale.getTotalAmount()));
        if (sale.getDiscountAmount() > 0) {
            sb.append(String.format("%-26s %10.2f%n", "Discount:", sale.getDiscountAmount()));
        }
        sb.append(String.format("%-26s %10.2f%n", "TOTAL:", sale.getFinalAmount()));
        sb.append(String.format("%-26s %10.2f%n", "Paid:", sale.getAmountPaid()));
        sb.append(String.format("%-26s %10.2f%n", "Change:", sale.getChangeAmount()));
        sb.append("========================================\n");
        sb.append("        Thank you for shopping!         \n");
        sb.append("========================================\n");

        return sb.toString();
    }
}
