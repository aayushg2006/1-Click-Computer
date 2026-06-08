package com.oneclick.repair.service;

import com.oneclick.repair.dto.ProductImageDTO;
import com.oneclick.repair.model.Product;
import com.oneclick.repair.model.ProductImage;
import com.oneclick.repair.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final MinioStorageService minioStorageService;
    private final StorageKeyService storageKeyService;

    @Transactional
    public List<ProductImageDTO> replaceProductImages(Product product, List<MultipartFile> files) {
        productImageRepository.deleteByProductId(product.getId());
        return saveProductImages(product, files);
    }

    @Transactional
    public List<ProductImageDTO> saveProductImages(Product product, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        String slug = buildSlug(product);
        boolean primaryAssigned = false;

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String objectKey = storageKeyService.productImageKey(slug, file.getOriginalFilename());
            minioStorageService.store(file, objectKey);
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .objectKey(objectKey)
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .isPrimary(!primaryAssigned)
                    .build();
            productImageRepository.save(image);
            primaryAssigned = true;
        }

        return toDtos(product.getId());
    }

    @Transactional(readOnly = true)
    public List<ProductImageDTO> getProductImages(UUID productId) {
        return toDtos(productId);
    }

    public String getPrimaryImageUrl(UUID productId) {
        return productImageRepository.findByProductIdOrderByIsPrimaryDescCreatedAtAsc(productId).stream()
                .findFirst()
                .map(image -> minioStorageService.presignedGetUrl(image.getObjectKey(), 60 * 24))
                .orElse(null);
    }

    private List<ProductImageDTO> toDtos(UUID productId) {
        return productImageRepository.findByProductIdOrderByIsPrimaryDescCreatedAtAsc(productId).stream()
                .map(image -> ProductImageDTO.builder()
                        .id(image.getId())
                        .imageUrl(minioStorageService.presignedGetUrl(image.getObjectKey(), 60 * 24))
                        .primaryImage(Boolean.TRUE.equals(image.getIsPrimary()))
                        .build())
                .toList();
    }

    private String buildSlug(Product product) {
        return product.getId() + "-" + product.getName();
    }
}
