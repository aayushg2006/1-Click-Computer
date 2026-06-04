package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DoorstepRepairResponse {
    private String trackingCode;
    private String message;
    private BigDecimal visitCharge;
}