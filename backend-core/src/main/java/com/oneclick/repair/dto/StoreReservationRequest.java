package com.oneclick.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class StoreReservationRequest {
    // The ID of the product they want to reserve
    @NotNull
    private UUID productId;
    
    // Details of the customer coming to the Nallasopara store
    @NotBlank
    private String customerName;
    @NotBlank
    private String customerPhone;
    
    // The time they plan to arrive (e.g., "Today, 4:00 PM - 6:00 PM")
    @NotBlank
    private String expectedPickupTimeslot;
}
