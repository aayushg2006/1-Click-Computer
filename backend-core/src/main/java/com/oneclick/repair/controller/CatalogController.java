package com.oneclick.repair.controller;

import com.oneclick.repair.dto.PublicProductDTO;
import com.oneclick.repair.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// "public" means this URL doesn't require a password or JWT token
@RequestMapping("/api/v1/public/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    // @GetMapping because the website is only ASKNG for data, not sending any.
    @GetMapping("/products")
    public ResponseEntity<List<PublicProductDTO>> getAllProducts() {
        
        // Get the safe list from the Chef and serve it to the Next.js frontend!
        List<PublicProductDTO> safeCatalog = catalogService.getPublicCatalog();
        return ResponseEntity.ok(safeCatalog);
    }
}