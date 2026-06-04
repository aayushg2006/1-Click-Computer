package com.oneclick.repair.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class PcBuildRequest {
    // The customer's name and phone number so you know who is asking for the quote
    private String customerName;
    private String customerPhone;
    
    // A list of the unique IDs for all the parts they selected (CPU, GPU, RAM, etc.)
    private List<UUID> selectedProductIds;
    
    // Their delivery pin code to check if they are in the Vasai-Virar delivery zone
    private String pinCode; 
}