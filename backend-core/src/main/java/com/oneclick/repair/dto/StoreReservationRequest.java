package com.oneclick.repair.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class StoreReservationRequest {
    // The ID of the product they want to reserve
    private UUID productId;
    
    // Details of the customer coming to the Nallasopara store
    private String customerName;
    private String customerPhone;
    
    // The time they plan to arrive (e.g., "Today, 4:00 PM - 6:00 PM")
    private String expectedPickupTimeslot;
}