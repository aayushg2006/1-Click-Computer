package com.oneclick.repair.service;

import com.oneclick.repair.dto.PublicProductDTO;
import com.oneclick.repair.model.Product;
import com.oneclick.repair.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ProductRepository productRepository;

    // readOnly = true makes it blazing fast because we aren't saving any new data
    @Transactional(readOnly = true)
    public List<PublicProductDTO> getPublicCatalog() {
        
        // Grab every product from the database
        List<Product> allProducts = productRepository.findAll();

        // Filter and pack them into the safe boxes
        return allProducts.stream()
                // ONLY show products where 'isAvailableForPickup' is true
                .filter(Product::getIsAvailableForPickup) 
                .map(product -> PublicProductDTO.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .brand(product.getBrand())
                        // We safely grab ONLY the sellingPrice
                        .price(product.getSellingPrice())
                        .stockQuantity(product.getCurrentStock())
                        // If stock is greater than 0, it's true!
                        .inStock(product.getCurrentStock() > 0) 
                        // Safely get the category name, avoiding crashes if the category is empty
                        .categoryName(product.getCategory() != null ? product.getCategory().getName() : "General")
                        .build())
                .collect(Collectors.toList());
    }
}