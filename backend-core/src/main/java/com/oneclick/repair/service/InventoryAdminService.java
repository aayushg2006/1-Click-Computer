package com.oneclick.repair.service;

import com.oneclick.repair.dto.AddProductRequest;
import com.oneclick.repair.dto.admin.AdminProductDTO;
import com.oneclick.repair.model.Category;
import com.oneclick.repair.model.Product;
import com.oneclick.repair.repository.CategoryRepository;
import com.oneclick.repair.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryAdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public String addNewProduct(AddProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Error: Category not found in the database."));

        Product newProduct = Product.builder()
                .category(category)
                .name(request.getName())
                .brand(request.getBrand())
                .buyingPrice(request.getBuyingPrice())
                .sellingPrice(request.getSellingPrice())
                .currentStock(request.getInitialStock() != null ? request.getInitialStock() : 0)
                .isAvailableForPickup(request.getIsAvailableForPickup() != null ? request.getIsAvailableForPickup() : true)
                .build();

        productRepository.save(newProduct);
        return "Successfully added " + newProduct.getName() + " to the inventory!";
    }

    @Transactional(readOnly = true)
    public List<AdminProductDTO> listProducts() {
        return productRepository.findAll().stream()
                .map(this::toAdminDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminProductDTO> searchProducts(String query) {
        String q = query == null ? "" : query.trim();
        return productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrderByCreatedAtDesc(q, q)
                .stream()
                .map(this::toAdminDto)
                .toList();
    }

    @Transactional
    public AdminProductDTO adjustStock(UUID productId, int delta) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int updatedStock = Math.max(0, product.getStockQuantity() + delta);
        product.setCurrentStock(updatedStock);
        return toAdminDto(productRepository.save(product));
    }

    @Transactional
    public AdminProductDTO updatePickupAvailability(UUID productId, boolean available) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsAvailableForPickup(available);
        return toAdminDto(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public AdminProductDTO getProduct(UUID productId) {
        return productRepository.findById(productId)
                .map(this::toAdminDto)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private AdminProductDTO toAdminDto(Product product) {
        return AdminProductDTO.builder()
                .id(product.getId())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "General")
                .name(product.getName())
                .brand(product.getBrand())
                .buyingPrice(product.getBuyingPrice())
                .sellingPrice(product.getSellingPrice())
                .currentStock(product.getStockQuantity())
                .availableForPickup(product.getIsAvailableForPickup())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
