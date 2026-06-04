package com.oneclick.repair.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageKeyServiceTest {

    private final StorageKeyService storageKeyService = new StorageKeyService();

    @Test
    void buildsInvoicePdfKey() {
        assertTrue(storageKeyService.invoicePdfKey("INV-001", "Invoice Final.pdf").contains("inv-001"));
    }

    @Test
    void buildsTicketMediaKey() {
        assertTrue(storageKeyService.ticketMediaKey("TK-ABC", "Photo.jpg").contains("tk-abc"));
    }
}
