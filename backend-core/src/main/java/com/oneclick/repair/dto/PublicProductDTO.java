package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.oneclick.repair.dto.ProductImageDTO;

@Data
@Builder
public class PublicProductDTO {
    private UUID id; // The website needs this ID so the customer can click "Reserve"
    private String name;
    private String brand;
    
    // We only send the final selling price
    private BigDecimal price; 
    
    // We send this so the Next.js site can say "Only 2 left in stock!"
    private Integer stockQuantity; 
    private Boolean inStock;
    
    private String categoryName;
    private String primaryImageUrl;
    private List<ProductImageDTO> images;
}
