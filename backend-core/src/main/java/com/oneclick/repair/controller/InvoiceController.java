package com.oneclick.repair.controller;

import com.oneclick.repair.dto.OfflineInvoiceSyncRequest;
import com.oneclick.repair.dto.SyncResponse;
import com.oneclick.repair.dto.admin.InvoiceAdminDTO;
import com.oneclick.repair.service.InvoiceAdminService;
import com.oneclick.repair.service.InvoiceDocumentService;
import com.oneclick.repair.service.InvoiceSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceSyncService invoiceSyncService;
    private final InvoiceAdminService invoiceAdminService;
    private final InvoiceDocumentService invoiceDocumentService;

    @PostMapping("/sync")
    public ResponseEntity<SyncResponse> syncOfflineInvoices(@Valid @RequestBody OfflineInvoiceSyncRequest request) {
        return ResponseEntity.ok(invoiceSyncService.processOfflineSync(request));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceAdminDTO>> listInvoices() {
        return ResponseEntity.ok(invoiceAdminService.listInvoices());
    }

    @GetMapping("/search")
    public ResponseEntity<List<InvoiceAdminDTO>> searchInvoices(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(invoiceAdminService.searchInvoices(query));
    }

    @GetMapping("/{invoiceNumber}")
    public ResponseEntity<InvoiceAdminDTO> findByInvoiceNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceAdminService.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found")));
    }

    @PostMapping(value = "/{invoiceNumber}/pdf", consumes = {"multipart/form-data"})
    public ResponseEntity<InvoiceAdminDTO> uploadInvoicePdf(
            @PathVariable String invoiceNumber,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(invoiceDocumentService.uploadInvoicePdf(invoiceNumber, file));
    }
}
