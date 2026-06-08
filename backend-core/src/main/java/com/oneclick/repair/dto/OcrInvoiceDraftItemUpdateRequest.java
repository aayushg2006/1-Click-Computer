package com.oneclick.repair.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OcrInvoiceDraftItemUpdateRequest {
    private UUID id;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
