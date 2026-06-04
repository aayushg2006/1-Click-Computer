package com.oneclick.repair.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class StoreReservationAdminDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private String customerName;
    private String customerPhone;
    private String pickupTimeslot;
    private String status;
    private OffsetDateTime reservedAt;
}
