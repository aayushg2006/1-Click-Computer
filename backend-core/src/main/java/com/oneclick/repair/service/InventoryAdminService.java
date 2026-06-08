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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InventoryAdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageService productImageService;

    @Transactional
    public String addNewProduct(AddProductRequest request) {
        return addNewProduct(request, List.of());
    }

    @Transactional
    public String addNewProduct(AddProductRequest request, List<MultipartFile> images) {
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

        Product saved = productRepository.save(newProduct);
        productImageService.saveProductImages(saved, images);
        return "Successfully added " + newProduct.getName() + " to the inventory!";
    }

    @Transactional
    public AdminProductDTO updateProduct(UUID productId, AddProductRequest request, List<MultipartFile> images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Error: Category not found in the database."));

        product.setCategory(category);
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setBuyingPrice(request.getBuyingPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setCurrentStock(request.getInitialStock() != null ? request.getInitialStock() : product.getStockQuantity());
        product.setIsAvailableForPickup(request.getIsAvailableForPickup() != null ? request.getIsAvailableForPickup() : product.getIsAvailableForPickup());

        Product saved = productRepository.save(product);
        if (images != null && !images.isEmpty()) {
            productImageService.replaceProductImages(saved, images);
        }
        return toAdminDto(saved);
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
        List<com.oneclick.repair.dto.ProductImageDTO> images = productImageService.getProductImages(product.getId());
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
                .primaryImageUrl(productImageService.getPrimaryImageUrl(product.getId()))
                .images(images)
                .build();
    }
}
