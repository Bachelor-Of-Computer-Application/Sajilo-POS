package com.possystem.sajilopos.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.possystem.sajilopos.model.Customer;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvoiceService {

    public String formatInvoiceNumber(String datePart, int sequence) {
        return "INV-" + datePart + "-" + String.format("%03d", sequence);
    }

    public void generateInvoicePdf(Sale sale, Customer customer, int pointsEarned) {
        try {
            Path invoicesDir = Paths.get("invoices");
            Files.createDirectories(invoicesDir);

            String fileName = sale.getInvoiceNo() + ".pdf";
            Path filePath = invoicesDir.resolve(fileName);

            PdfWriter writer = new PdfWriter(Files.newOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("SAJILO POS"));
            document.add(new Paragraph("Invoice: " + sale.getInvoiceNo()));
            document.add(new Paragraph("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))));
            document.add(new Paragraph("Customer: " + (customer != null ? customer.getCustomerName() : "Walk-in Customer")));
            document.add(new Paragraph(""));

            for (SaleItem item : sale.getItems()) {
                document.add(new Paragraph(item.getProductName() + " | Qty: " + item.getQuantity()
                        + " | Unit: " + item.getUnitPrice() + " | Total: " + item.getSubtotal()));
            }

            document.add(new Paragraph(""));
            document.add(new Paragraph("Subtotal: " + sale.getTotalAmount()));
            document.add(new Paragraph("Discount: " + sale.getDiscountAmount()));
            document.add(new Paragraph("Grand Total: " + sale.getFinalAmount()));
            document.add(new Paragraph("Payment: " + sale.getPaymentMethod()));
            document.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to generate invoice PDF", e);
        }
    }
}
