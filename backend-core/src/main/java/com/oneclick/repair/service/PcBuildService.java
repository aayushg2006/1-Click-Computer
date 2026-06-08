package com.oneclick.repair.service;

import com.oneclick.repair.dto.PcBuildRequest;
import com.oneclick.repair.dto.PcBuildResponse;
import com.oneclick.repair.dto.PublicProductDTO;
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
    private final ServiceAreaService serviceAreaService;
    private final WhatsAppMessageService whatsAppMessageService;
    private final ProductImageService productImageService;

    @Transactional
    public PcBuildResponse generateBuildQuote(PcBuildRequest request) {
        boolean deliverable = serviceAreaService.isDeliverablePinCode(request.getPinCode());

        String refId = "PC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<Product> selectedProducts = new ArrayList<>();
        StringBuilder partsText = new StringBuilder();

        for (UUID productId : request.getSelectedProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found!"));

            selectedProducts.add(product);
            grandTotal = grandTotal.add(product.getSellingPrice());
            partsText.append("- ").append(product.getName())
                    .append(" (INR ").append(product.getSellingPrice()).append(")\n");
        }

        PcBuildConfiguration configuration = PcBuildConfiguration.builder()
                .referenceId(refId)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .totalPartsCost(grandTotal)
                .status("PENDING_NEGOTIATION")
                .build();

        PcBuildConfiguration savedConfig = buildRepository.save(configuration);

        for (Product product : selectedProducts) {
            PcBuildItem item = PcBuildItem.builder()
                    .buildConfiguration(savedConfig)
                    .product(product)
                    .lockedSellingPrice(product.getSellingPrice())
                    .build();
            buildItemRepository.save(item);
        }

        String whatsappText = whatsAppMessageService.formatPcBuildNegotiation(
                refId,
                partsText.toString(),
                "INR " + grandTotal,
                request.getPinCode(),
                deliverable
        );

        return PcBuildResponse.builder()
                .referenceId(refId)
                .totalPrice(grandTotal)
                .whatsappMessage(whatsappText)
                .selectedProducts(selectedProducts.stream().map(this::toPublicProductDto).toList())
                .build();
    }

    private PublicProductDTO toPublicProductDto(Product product) {
        return PublicProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .price(product.getSellingPrice())
                .stockQuantity(product.getStockQuantity())
                .inStock(product.getStockQuantity() > 0)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "General")
                .primaryImageUrl(productImageService.getPrimaryImageUrl(product.getId()))
                .images(productImageService.getProductImages(product.getId()))
                .build();
    }
}
