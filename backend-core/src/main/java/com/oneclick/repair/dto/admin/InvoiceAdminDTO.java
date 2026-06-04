package com.oneclick.repair.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class InvoiceAdminDTO {
    private UUID id;
    private String invoiceNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal subTotal;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String pdfFilePath;
    private OffsetDateTime createdAt;
}
