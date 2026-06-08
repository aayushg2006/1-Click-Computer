package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OcrExtractionResultDTO {
    private UUID jobId;
    private String status;
    private String sourceFileName;
    private String sourceContentType;
    private String objectKey;
    private String extractedText;
    private String vendorName;
    private String invoiceNumber;
    private String invoiceDate;
    private String customerName;
    private String customerPhone;
    private List<OcrInvoiceItemDTO> items;
    private BigDecimal subTotal;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private String rawModelResponse;
    private String errorMessage;
}
