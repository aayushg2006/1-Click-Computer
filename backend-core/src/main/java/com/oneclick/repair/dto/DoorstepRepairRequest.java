package com.oneclick.repair.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoorstepRepairRequest {
    @NotBlank
    private String customerName;
    @NotBlank
    private String customerPhone;
    @NotBlank
    private String deviceDetails;
    @NotBlank
    private String address;
    @NotBlank
    private String pinCode; // To verify if they are in the Vasai-Virar zone
}
