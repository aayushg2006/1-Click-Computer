package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreReservationResponse {
    // The unique ID of their reservation so they can show it at the counter
    private String reservationId; 
    
    // The name of the item they reserved (e.g., "Logitech G102 Mouse")
    private String productName;
    
    // Will say "HELD" if successful
    private String status;
    
    // A friendly message to display on the Next.js screen
    private String message; 
}