package com.oneclick.repair.service;

import com.oneclick.repair.dto.admin.InvoiceAdminDTO;
import com.oneclick.repair.model.Invoice;
import com.oneclick.repair.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InvoiceDocumentService {

    private final InvoiceRepository invoiceRepository;
    private final MinioStorageService minioStorageService;
    private final StorageKeyService storageKeyService;
    private final InvoiceAdminService invoiceAdminService;

    @Transactional
    public InvoiceAdminDTO uploadInvoicePdf(String invoiceNumber, MultipartFile file) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String objectKey = storageKeyService.invoicePdfKey(invoiceNumber, file.getOriginalFilename());
        minioStorageService.store(file, objectKey);
        invoice.setPdfFilePath(objectKey);
        invoiceRepository.save(invoice);

        return invoiceAdminService.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Transactional(readOnly = true)
    public String buildInvoicePdfUrl(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        if (invoice.getPdfFilePath() == null || invoice.getPdfFilePath().isBlank()) {
            throw new RuntimeException("Invoice PDF not uploaded yet");
        }
        return minioStorageService.presignedGetUrl(invoice.getPdfFilePath(), 60 * 24);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceEntity(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
}
