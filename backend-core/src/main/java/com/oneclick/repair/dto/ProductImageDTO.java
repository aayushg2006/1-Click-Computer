package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductImageDTO {
    private UUID id;
    private String imageUrl;
    private boolean primaryImage;
}
