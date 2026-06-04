package com.oneclick.repair.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketUpdateDTO {
    private UUID id;
    private String statusTitle;
    private String statusDescription;
    private String mediaUrl;
    private Boolean visibleToCustomer;
    private OffsetDateTime createdAt;
}
