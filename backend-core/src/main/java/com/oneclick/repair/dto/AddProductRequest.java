package com.oneclick.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String brand;
    
    // The ID of the category this belongs to (e.g., "Mice", "Keyboards")
    @NotNull
    private UUID categoryId;
    
    // Your secret wholesale cost
    @NotNull
    private BigDecimal buyingPrice;
    
    // The public price the customer pays
    @NotNull
    private BigDecimal sellingPrice;
    
    private Integer initialStock;
    
    private Boolean isAvailableForPickup;
}
