package com.oneclick.repair.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketUpdateRequest {
    @NotBlank
    private String statusTitle;

    @NotBlank
    private String statusDescription;

    private String mediaUrl;
    private Boolean visibleToCustomer = true;
}
