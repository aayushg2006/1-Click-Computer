package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CctvEstimatorResponse {
    private String referenceId;
    private Integer cameraCount;
    private Integer backupDays;
    private BigDecimal estimatedAmount;
    private Boolean withinServiceArea;
    private String leadMessage;
    private UUID ticketId;
}
