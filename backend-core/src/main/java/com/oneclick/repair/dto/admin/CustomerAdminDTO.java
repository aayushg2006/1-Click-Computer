package com.oneclick.repair.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerAdminDTO {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String address;
    private BigDecimal currentBalance;
    private OffsetDateTime createdAt;
}
