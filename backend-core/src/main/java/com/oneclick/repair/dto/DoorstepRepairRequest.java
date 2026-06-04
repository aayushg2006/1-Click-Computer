package com.oneclick.repair.dto;

import lombok.Data;

@Data
public class DoorstepRepairRequest {
    private String customerName;
    private String customerPhone;
    private String deviceDetails;
    private String address;
    private String pinCode; // To verify if they are in the Vasai-Virar zone
}