package com.possystem.sajilopos.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvoiceServiceTest {

    @Test
    void formatInvoiceNumberUsesDateAndSequence() {
        InvoiceService service = new InvoiceService();

        String invoiceNo = service.formatInvoiceNumber("20260712", 7);

        assertEquals("INV-20260712-007", invoiceNo);
    }
}
