package com.oneclick.repair.service;

import com.oneclick.repair.dto.PcBuildRequest;
import com.oneclick.repair.dto.PcBuildResponse;
import com.oneclick.repair.model.PcBuildConfiguration;
import com.oneclick.repair.model.PcBuildItem;
import com.oneclick.repair.model.Product;
import com.oneclick.repair.repository.PcBuildConfigurationRepository;
import com.oneclick.repair.repository.PcBuildItemRepository;
import com.oneclick.repair.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PcBuildService {

    private final ProductRepository productRepository;
    private final PcBuildConfigurationRepository buildRepository;
    private final PcBuildItemRepository buildItemRepository;

    @Transactional
    public PcBuildResponse generateBuildQuote(PcBuildRequest request) {
        
        // Create a short, readable ID for this specific PC build
        String refId = "PC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        BigDecimal grandTotal = BigDecimal.ZERO;
        List<Product> selectedProducts = new ArrayList<>();
        StringBuilder whatsappText = new StringBuilder();
        
        whatsappText.append("Hello 1 Click Computer! I would like to finalize my Custom PC Build.\n\n");
        whatsappText.append("*Reference ID:* ").append(refId).append("\n");
        whatsappText.append("*Parts Selected:*\n");

        // Loop through all the parts the customer picked
        for (UUID productId : request.getSelectedProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found!"));
            
            selectedProducts.add(product);
            
            // FIX: Using getSellingPrice() matching your Product model
            grandTotal = grandTotal.add(product.getSellingPrice());
            
            whatsappText.append("- ").append(product.getName())
                        .append(" (₹").append(product.getSellingPrice()).append(")\n");
        }

        // Save the main configuration using the new slots we just added
        PcBuildConfiguration configuration = PcBuildConfiguration.builder()
                .referenceId(refId)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                // FIX: Matches totalPartsCost in your PcBuildConfiguration model
                .totalPartsCost(grandTotal)
                .status("PENDING_NEGOTIATION")
                .build();
        
        PcBuildConfiguration savedConfig = buildRepository.save(configuration);

        // Save each specific part linking it to the main build
        for (Product product : selectedProducts) {
            PcBuildItem item = PcBuildItem.builder()
                    // FIX: Matches buildConfiguration in your PcBuildItem model
                    .buildConfiguration(savedConfig)
                    .product(product)
                    // FIX: Matches lockedSellingPrice in your PcBuildItem model
                    .lockedSellingPrice(product.getSellingPrice()) 
                    .build();
            buildItemRepository.save(item);
        }

        whatsappText.append("\n*Estimated Total:* ₹").append(grandTotal);
        whatsappText.append("\nMy Pin Code is: ").append(request.getPinCode());

        // Box up the final response and send it to the Controller
        return PcBuildResponse.builder()
                .referenceId(refId)
                .totalPrice(grandTotal)
                .whatsappMessage(whatsappText.toString())
                .build();
    }
}