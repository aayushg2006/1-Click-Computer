package com.oneclick.repair.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyncResponse {
    private String message;
    private int totalInvoicesSynced;
}