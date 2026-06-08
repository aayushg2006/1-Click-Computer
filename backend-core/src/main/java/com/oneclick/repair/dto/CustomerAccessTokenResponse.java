package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class CustomerAccessTokenResponse {
    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;
    private String trackingCode;
    private OffsetDateTime issuedAt;
}
