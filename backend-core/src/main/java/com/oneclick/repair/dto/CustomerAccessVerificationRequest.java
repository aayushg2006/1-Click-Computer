package com.oneclick.repair.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerAccessVerificationRequest {
    @NotBlank
    private String trackingCode;

    @NotBlank
    private String mobileNumber;
}
