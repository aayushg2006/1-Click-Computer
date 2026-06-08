package com.oneclick.repair.controller;

import com.oneclick.repair.dto.OcrIntakeResponse;
import com.oneclick.repair.dto.OcrExtractionResultDTO;
import com.oneclick.repair.dto.OcrInvoiceDraftDTO;
import com.oneclick.repair.dto.OcrInvoiceDraftUpdateRequest;
import com.oneclick.repair.service.InvoiceOcrIntakeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/ocr")
@RequiredArgsConstructor
public class OcrInvoiceController {

    private final InvoiceOcrIntakeService invoiceOcrIntakeService;

    @PostMapping(value = "/invoices/intake", consumes = {"multipart/form-data"})
    public ResponseEntity<OcrIntakeResponse> intake(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(invoiceOcrIntakeService.intake(file));
    }

    @PostMapping(value = "/invoices/{jobId}/extract", consumes = {"multipart/form-data"})
    public ResponseEntity<OcrExtractionResultDTO> extract(@PathVariable UUID jobId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(invoiceOcrIntakeService.extract(jobId, file));
    }

    @GetMapping("/invoices/{jobId}/draft")
    public ResponseEntity<OcrInvoiceDraftDTO> getDraft(@PathVariable UUID jobId) {
        return ResponseEntity.ok(invoiceOcrIntakeService.getDraft(jobId));
    }

    @PatchMapping("/invoices/{jobId}/draft")
    public ResponseEntity<OcrInvoiceDraftDTO> updateDraft(@PathVariable UUID jobId, @Valid @RequestBody OcrInvoiceDraftUpdateRequest request) {
        return ResponseEntity.ok(invoiceOcrIntakeService.updateDraft(jobId, request));
    }

    @PostMapping("/invoices/{jobId}/approve")
    public ResponseEntity<OcrInvoiceDraftDTO> approveDraft(@PathVariable UUID jobId) {
        return ResponseEntity.ok(invoiceOcrIntakeService.approveDraft(jobId));
    }

    @PostMapping("/invoices/{jobId}/reject")
    public ResponseEntity<OcrInvoiceDraftDTO> rejectDraft(@PathVariable UUID jobId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(invoiceOcrIntakeService.rejectDraft(jobId, reason));
    }

    @PostMapping("/invoices/{jobId}/commit")
    public ResponseEntity<OcrInvoiceDraftDTO> commitDraft(@PathVariable UUID jobId) {
        return ResponseEntity.ok(invoiceOcrIntakeService.commitDraft(jobId));
    }
}
