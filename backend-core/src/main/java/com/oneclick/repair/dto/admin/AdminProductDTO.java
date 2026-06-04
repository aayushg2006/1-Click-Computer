package com.oneclick.repair.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminProductDTO {
    private UUID id;
    private String categoryName;
    private String name;
    private String brand;
    private BigDecimal buyingPrice;
    private BigDecimal sellingPrice;
    private Integer currentStock;
    private Boolean availableForPickup;
    private OffsetDateTime createdAt;
}
