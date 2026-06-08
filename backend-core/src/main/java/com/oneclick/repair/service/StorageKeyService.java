package com.oneclick.repair.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class StorageKeyService {

    public String invoicePdfKey(String invoiceNumber, String originalFilename) {
        return "invoices/" + safe(invoiceNumber) + "/" + safe(originalFilename);
    }

    public String ticketMediaKey(String trackingCode, String originalFilename) {
        return "tickets/" + safe(trackingCode) + "/" + safe(originalFilename);
    }

    public String productImageKey(String productSlug, String originalFilename) {
        return "products/" + safe(productSlug) + "/" + safe(originalFilename);
    }

    private String safe(String value) {
        return value == null ? "unknown" : value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "-");
    }
}
