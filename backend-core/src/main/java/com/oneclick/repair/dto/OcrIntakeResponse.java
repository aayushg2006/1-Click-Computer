package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcrIntakeResponse {
    private String documentId;
    private String objectKey;
    private String status;
    private String message;
}
