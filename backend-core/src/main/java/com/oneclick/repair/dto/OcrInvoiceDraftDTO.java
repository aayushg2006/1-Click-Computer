package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OcrInvoiceDraftDTO {
    private UUID id;
    private UUID jobId;
    private String status;
    private String vendorName;
    private String invoiceNumber;
    private String invoiceDate;
    private String customerName;
    private String customerPhone;
    private BigDecimal subTotal;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private String extractedText;
    private String rawModelResponse;
    private String rejectionReason;
    private OffsetDateTime reviewedAt;
    private OffsetDateTime committedAt;
    private List<OcrInvoiceDraftItemDTO> items;
}
