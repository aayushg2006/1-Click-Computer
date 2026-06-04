package com.oneclick.repair.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketAdminDTO {
    private UUID id;
    private String trackingCode;
    private String customerName;
    private String customerPhone;
    private String ticketType;
    private String status;
    private String deviceDetails;
    private BigDecimal estimatedCost;
    private String assignedTechnicianName;
    private Boolean isHomeService;
    private BigDecimal visitCharge;
    private OffsetDateTime createdAt;
}
