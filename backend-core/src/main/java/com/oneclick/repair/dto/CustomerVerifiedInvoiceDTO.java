package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class CustomerVerifiedInvoiceDTO {
    private String invoiceNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal subTotal;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String pdfFilePath;
    private String pdfUrl;
    private OffsetDateTime createdAt;
    private List<CustomerVerifiedInvoiceItemDTO> items;
}
