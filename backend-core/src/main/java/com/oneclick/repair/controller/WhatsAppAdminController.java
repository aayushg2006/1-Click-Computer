package com.oneclick.repair.controller;

import com.oneclick.repair.service.WhatsAppAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/whatsapp")
@RequiredArgsConstructor
public class WhatsAppAdminController {

    private final WhatsAppAdminService whatsAppAdminService;

    @PostMapping("/invoices/{invoiceNumber}/send")
    public ResponseEntity<String> sendInvoice(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(whatsAppAdminService.sendInvoicePdf(invoiceNumber));
    }

    @PostMapping("/customers/{customerId}/reminder")
    public ResponseEntity<String> sendLedgerReminder(@PathVariable UUID customerId) {
        return ResponseEntity.ok(whatsAppAdminService.sendLedgerReminder(customerId));
    }

    @PostMapping("/reminder")
    public ResponseEntity<String> sendCustomReminder(
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(whatsAppAdminService.sendPaymentReminder(phoneNumber, customerName, note));
    }
}
