package com.oneclick.repair.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CctvEstimatorRequest {
    @NotNull
    @Min(1)
    private Integer cameraCount;

    @NotNull
    @Min(1)
    private Integer backupDays;

    private String customerName;
    private String customerPhone;
    private String address;
    private String pinCode;
}
