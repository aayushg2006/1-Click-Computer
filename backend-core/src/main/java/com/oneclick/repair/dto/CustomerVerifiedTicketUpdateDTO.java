package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class CustomerVerifiedTicketUpdateDTO {
    private String id;
    private String statusTitle;
    private String statusDescription;
    private String mediaUrl;
    private Boolean visibleToCustomer;
    private OffsetDateTime createdAt;
}
