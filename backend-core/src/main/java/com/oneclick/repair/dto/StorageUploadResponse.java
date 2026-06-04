package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageUploadResponse {
    private String bucket;
    private String objectKey;
    private String url;
    private String contentType;
}
