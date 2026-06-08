package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class CustomerVerifiedTicketDTO {
    private String trackingCode;
    private String customerName;
    private String customerPhone;
    private String ticketType;
    private String status;
    private String deviceDetails;
    private List<String> conditionPhotoUrls;
    private BigDecimal estimatedCost;
    private BigDecimal visitCharge;
    private OffsetDateTime createdAt;
    private List<CustomerVerifiedTicketUpdateDTO> updates;
}
