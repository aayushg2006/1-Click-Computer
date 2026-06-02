package com.oneclick.repair.repository;

import com.oneclick.repair.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    // Finds all products belonging to a specific category (e.g., all "Monitors")
    List<Product> findByCategoryId(UUID categoryId);

    // Finds items that are physically in the shop and available for immediate Vasai-Virar pickup
    List<Product> findByCurrentStockGreaterThanAndIsAvailableForPickupTrue(Integer stockThreshold);
    
    // Find a specific product by its brand and name
    Optional<Product> findByBrandAndName(String brand, String name);
}