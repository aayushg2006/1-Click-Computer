package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TicketTrackingDTO {
    // We only send the tracking code, NOT the UUID
    private String trackingCode;
    
    // We mask the name (e.g., "Aayush G.") for privacy on a public link
    private String customerNameMasked; 
    
    private String ticketType;
    private String status; // "RECEIVED", "DIAGNOSING", "REPAIRED"
    private String deviceDetails;
    
    // The URLs of the photos so the Next.js frontend can display them
    private List<String> conditionPhotoUrls;
    
    // Only show the estimated cost if it has been approved
    private String approvedEstimatedCost;
}