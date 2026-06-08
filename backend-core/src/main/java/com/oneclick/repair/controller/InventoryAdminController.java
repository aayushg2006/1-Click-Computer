package com.oneclick.repair.controller;

import com.oneclick.repair.dto.AddProductRequest;
import com.oneclick.repair.dto.admin.AdminProductDTO;
import com.oneclick.repair.service.InventoryAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
public class InventoryAdminController {

    private final InventoryAdminService inventoryAdminService;

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@Valid @RequestBody AddProductRequest request) {
        return ResponseEntity.ok(inventoryAdminService.addNewProduct(request));
    }

    @PostMapping(value = "/add-with-images", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addProductWithImages(
            @RequestPart("product") @Valid AddProductRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(inventoryAdminService.addNewProduct(request, images));
    }

    @PutMapping(value = "/{productId}", consumes = {"multipart/form-data"})
    public ResponseEntity<AdminProductDTO> updateProductWithImages(
            @PathVariable UUID productId,
            @RequestPart("product") @Valid AddProductRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(inventoryAdminService.updateProduct(productId, request, images));
    }

    @GetMapping
    public ResponseEntity<List<AdminProductDTO>> listProducts() {
        return ResponseEntity.ok(inventoryAdminService.listProducts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AdminProductDTO>> searchProducts(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(inventoryAdminService.searchProducts(query));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<AdminProductDTO> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(inventoryAdminService.getProduct(productId));
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<AdminProductDTO> adjustStock(@PathVariable UUID productId, @RequestParam int delta) {
        return ResponseEntity.ok(inventoryAdminService.adjustStock(productId, delta));
    }

    @PutMapping("/{productId}/pickup-availability")
    public ResponseEntity<AdminProductDTO> updatePickupAvailability(@PathVariable UUID productId, @RequestParam boolean available) {
        return ResponseEntity.ok(inventoryAdminService.updatePickupAvailability(productId, available));
    }
}
