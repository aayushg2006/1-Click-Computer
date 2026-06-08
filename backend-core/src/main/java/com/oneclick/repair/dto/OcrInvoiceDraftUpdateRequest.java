package com.oneclick.repair.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OcrInvoiceDraftUpdateRequest {
    private String vendorName;
    private String invoiceNumber;
    private String invoiceDate;
    private String customerName;
    private String customerPhone;
    private BigDecimal subTotal;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;

    @Valid
    @NotNull
    private List<OcrInvoiceDraftItemUpdateRequest> items;
}
