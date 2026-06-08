package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PcBuildResponse {
    // A short, easy-to-read ID like "PC-98X2" so you can look it up in your app
    private String referenceId;
    
    // The grand total of all the parts
    private BigDecimal totalPrice;
    
    // This is the magic part: a fully formatted text string ready to be sent on WhatsApp
    private String whatsappMessage; 

    private List<PublicProductDTO> selectedProducts;
}
