package com.oneclick.repair.service;

import com.oneclick.repair.dto.admin.InvoiceAdminDTO;
import com.oneclick.repair.model.Invoice;
import com.oneclick.repair.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceAdminService {

    private final InvoiceRepository invoiceRepository;

    @Transactional(readOnly = true)
    public List<InvoiceAdminDTO> listInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InvoiceAdminDTO> searchInvoices(String query) {
        String q = query == null ? "" : query.trim();
        return invoiceRepository.findByInvoiceNumberContainingIgnoreCaseOrCustomerPhoneNumberContainingIgnoreCaseOrderByCreatedAtDesc(q, q)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<InvoiceAdminDTO> findByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber).map(this::toDto);
    }

    private InvoiceAdminDTO toDto(Invoice invoice) {
        return InvoiceAdminDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : null)
                .customerPhone(invoice.getCustomer() != null ? invoice.getCustomer().getPhoneNumber() : null)
                .subTotal(invoice.getSubTotal())
                .gstAmount(invoice.getGstAmount())
                .totalAmount(invoice.getTotalAmount())
                .paymentStatus(invoice.getPaymentStatus())
                .pdfFilePath(invoice.getPdfFilePath())
                .createdAt(invoice.getCreatedAt())
                .build();
    }
}
